package com.exemplo.website.service;

import com.exemplo.website.dto.evento.EventoRequest;
import com.exemplo.website.dto.evento.EventoResponse;
import com.exemplo.website.dto.evento.EventoUpdateRequest;
import com.exemplo.website.model.Evento;
import com.exemplo.website.model.LocalEndereco;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.repository.EventoRepository;
import com.exemplo.website.repository.LocalEnderecoRepository;
import com.exemplo.website.repository.UsuarioRepository;
import com.exemplo.website.security.AccessPolicyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final LocalEnderecoRepository localEnderecoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AccessPolicyService accessPolicyService;

    public EventoResponse cadastrar(EventoRequest request, Long usuarioAutenticadoId) {

        validarDatas(
                request.dataHoraInicio(),
                request.dataHoraFim()
        );

        Usuario usuario = usuarioRepository
                .findById(request.usuarioId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Usuário não encontrado"
                        )
                );

        LocalEndereco localEndereco =
                localEnderecoRepository
                        .findById(request.localEnderecoId())
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Local não encontrado"
                                )
                        );

        accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, usuario.getUnidade().getId());
        accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, localEndereco.getUnidade().getId());
        if (!usuario.getUnidade().getId().equals(localEndereco.getUnidade().getId())) {
            throw new IllegalArgumentException("O usuário e o local devem pertencer à mesma unidade");
        }

        Evento evento = new Evento();

        evento.setUsuario(usuario);
        evento.setLocalEndereco(localEndereco);
        evento.setTitulo(request.titulo().trim());
        evento.setDescricao(request.descricao().trim());
        evento.setDescricaoLocal(
                request.descricaoLocal().trim()
        );
        evento.setObservacao(request.observacao());
        evento.setDataHoraInicio(
                request.dataHoraInicio()
        );
        evento.setDataHoraFim(
                request.dataHoraFim()
        );

        return toResponse(eventoRepository.save(evento));
    }

    public List<EventoResponse> listarTodos(Long usuarioAutenticadoId) {
        List<Evento> eventos = accessPolicyService.ehAdministrador(usuarioAutenticadoId)
                ? eventoRepository.findAll()
                : eventoRepository.findByLocalEndereco_Unidade_Id(accessPolicyService.unidadeDoUsuarioAutenticado(usuarioAutenticadoId));
        return eventos
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<EventoResponse> listarPorUsuario(
            Long usuarioId,
            Long usuarioAutenticadoId
    ) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, usuario.getUnidade().getId());

        return eventoRepository
                .findByUsuario_Id(usuarioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public EventoResponse buscarPorId(Long id, Long usuarioAutenticadoId) {
        Evento evento = buscarEntidade(id);
        validarAcesso(usuarioAutenticadoId, evento);
        return toResponse(evento);
    }

    public EventoResponse atualizar(
            Long id,
            EventoUpdateRequest request,
            Long usuarioAutenticadoId
    ) {

        Evento evento = buscarEntidade(id);
        validarAcesso(usuarioAutenticadoId, evento);

        if (request.localEnderecoId() != null) {

            LocalEndereco localEndereco =
                    localEnderecoRepository
                            .findById(request.localEnderecoId())
                            .orElseThrow(() ->
                                    new EntityNotFoundException(
                                            "Local não encontrado"
                                    )
                            );

            evento.setLocalEndereco(localEndereco);
            accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, localEndereco.getUnidade().getId());
            if (!evento.getUsuario().getUnidade().getId().equals(localEndereco.getUnidade().getId())) {
                throw new IllegalArgumentException("O usuário e o local devem pertencer à mesma unidade");
            }
        }

        if (request.titulo() != null) {
            evento.setTitulo(request.titulo().trim());
        }

        if (request.descricao() != null) {
            evento.setDescricao(
                    request.descricao().trim()
            );
        }

        if (request.descricaoLocal() != null) {
            evento.setDescricaoLocal(
                    request.descricaoLocal().trim()
            );
        }

        if (request.observacao() != null) {
            evento.setObservacao(
                    request.observacao()
            );
        }

        LocalDateTime inicio =
                request.dataHoraInicio() != null
                        ? request.dataHoraInicio()
                        : evento.getDataHoraInicio();

        LocalDateTime fim =
                request.dataHoraFim() != null
                        ? request.dataHoraFim()
                        : evento.getDataHoraFim();

        validarDatas(inicio, fim);

        evento.setDataHoraInicio(inicio);
        evento.setDataHoraFim(fim);

        return toResponse(eventoRepository.save(evento));
    }

    public void deletar(Long id, Long usuarioAutenticadoId) {

        Evento evento = buscarEntidade(id);
        validarAcesso(usuarioAutenticadoId, evento);

        eventoRepository.delete(evento);
    }

    private Evento buscarEntidade(Long id) {

        return eventoRepository
                .findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Evento não encontrado"
                        )
                );
    }

    private void validarDatas(
            LocalDateTime inicio,
            LocalDateTime fim
    ) {

        if (inicio == null || fim == null) {
            throw new IllegalArgumentException(
                    "As datas de início e fim são obrigatórias"
            );
        }

        if (!fim.isAfter(inicio)) {
            throw new IllegalArgumentException(
                    "A data de fim deve ser posterior à data de início"
            );
        }
    }

    private void validarAcesso(Long usuarioAutenticadoId, Evento evento) {
        accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, evento.getUsuario().getUnidade().getId());
        accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, evento.getLocalEndereco().getUnidade().getId());
    }

    private EventoResponse toResponse(Evento evento) {

        return new EventoResponse(
                evento.getId(),
                evento.getUsuario().getId(),
                evento.getLocalEndereco().getId(),
                evento.getTitulo(),
                evento.getDescricao(),
                evento.getDescricaoLocal(),
                evento.getObservacao(),
                evento.getDataHoraInicio(),
                evento.getDataHoraFim(),
                evento.getDataCriacao()
        );
    }
}
