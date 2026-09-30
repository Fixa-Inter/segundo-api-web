package com.exemplo.website.controller;

import com.exemplo.website.dto.evento.EventoRequest;
import com.exemplo.website.dto.evento.EventoResponse;
import com.exemplo.website.dto.evento.EventoUpdateRequest;
import com.exemplo.website.service.EventoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping
    public ResponseEntity<EventoResponse> cadastrar(
            @RequestBody EventoRequest request
    ) {
        EventoResponse response = eventoService.cadastrar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<EventoResponse>> listarTodos() {
        return ResponseEntity.ok(eventoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponse> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(eventoService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EventoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody EventoUpdateRequest request
    ) {
        return ResponseEntity.ok(
                eventoService.atualizar(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id
    ) {
        eventoService.deletar(id);

        return ResponseEntity.noContent().build();
    }
}