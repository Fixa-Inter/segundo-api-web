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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final LocalEnderecoRepository localEnderecoRepository;
    private final UsuarioRepository usuarioRepository;

    public EventoResponse cadastrar(EventoRequest request) {

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

    public List<EventoResponse> listarTodos() {
        return eventoRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<EventoResponse> listarPorUsuario(
            Long usuarioId
    ) {

        return eventoRepository
                .findByUsuario_Id(usuarioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public EventoResponse buscarPorId(Long id) {
        return toResponse(buscarEntidade(id));
    }

    public EventoResponse atualizar(
            Long id,
            EventoUpdateRequest request
    ) {

        Evento evento = buscarEntidade(id);

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

    public void deletar(Long id) {

        Evento evento = buscarEntidade(id);

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