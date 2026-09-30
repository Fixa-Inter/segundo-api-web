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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LocalEnderecoRepository localEnderecoRepository;

    public EventoService(
            EventoRepository eventoRepository,
            UsuarioRepository usuarioRepository,
            LocalEnderecoRepository localEnderecoRepository
    ) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
        this.localEnderecoRepository = localEnderecoRepository;
    }

    public EventoResponse cadastrar(EventoRequest request) {

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Usuário não encontrado"));

        LocalEndereco localEndereco = localEnderecoRepository
                .findById(request.localEnderecoId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Local não encontrado"));

        validarDatas(request);

        Evento evento = new Evento();

        evento.setUsuario(usuario);
        evento.setLocalEndereco(localEndereco);
        evento.setTitulo(request.titulo());
        evento.setDescricao(request.descricao());
        evento.setDescricaoLocal(request.descricaoLocal());
        evento.setObservacao(request.observacao());
        evento.setDataHoraInicio(request.dataHoraInicio());
        evento.setDataHoraFim(request.dataHoraFim());

        Evento eventoSalvo = eventoRepository.save(evento);

        return toResponse(eventoSalvo);
    }

    public List<EventoResponse> listarTodos() {
        return eventoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public EventoResponse buscarPorId(Long id) {

        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Evento não encontrado"));

        return toResponse(evento);
    }

    private void validarDatas(EventoRequest request) {

        if (request.dataHoraInicio() == null ||
                request.dataHoraFim() == null) {
            throw new IllegalArgumentException(
                    "Data de início e fim são obrigatórias"
            );
        }

        if (!request.dataHoraFim().isAfter(request.dataHoraInicio())) {
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

    public EventoResponse atualizar(Long id, EventoUpdateRequest request) {

        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Evento não encontrado"));

        if (request.localEnderecoId() != null) {
            LocalEndereco localEndereco = localEnderecoRepository
                    .findById(request.localEnderecoId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Local não encontrado"));

            evento.setLocalEndereco(localEndereco);
        }

        if (request.titulo() != null) {
            evento.setTitulo(request.titulo());
        }

        if (request.descricao() != null) {
            evento.setDescricao(request.descricao());
        }

        if (request.descricaoLocal() != null) {
            evento.setDescricaoLocal(request.descricaoLocal());
        }

        if (request.observacao() != null) {
            evento.setObservacao(request.observacao());
        }

        if (request.dataHoraInicio() != null) {
            evento.setDataHoraInicio(request.dataHoraInicio());
        }

        if (request.dataHoraFim() != null) {
            evento.setDataHoraFim(request.dataHoraFim());
        }

        validarDatasAtualizacao(evento);

        Evento eventoAtualizado = eventoRepository.save(evento);

        return toResponse(eventoAtualizado);
    }

    private void validarDatasAtualizacao(Evento evento) {

        if (evento.getDataHoraInicio() == null ||
                evento.getDataHoraFim() == null) {
            throw new IllegalArgumentException(
                    "Data de início e fim são obrigatórias"
            );
        }

        if (!evento.getDataHoraFim().isAfter(evento.getDataHoraInicio())) {
            throw new IllegalArgumentException(
                    "A data de fim deve ser posterior à data de início"
            );
        }
    }

    public void deletar(Long id) {

        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Evento não encontrado"));

        eventoRepository.delete(evento);
    }

}