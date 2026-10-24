package com.exemplo.website.controller;

import com.exemplo.website.dto.evento.EventoRequest;
import com.exemplo.website.dto.evento.EventoResponse;
import com.exemplo.website.dto.evento.EventoUpdateRequest;
import com.exemplo.website.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@PreAuthorize("hasAnyRole('GESTOR', 'ADMINISTRADOR')")
@RequestMapping("/api/v1/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping
    public ResponseEntity<EventoResponse> cadastrar(
            @Valid @RequestBody EventoRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        EventoResponse response = eventoService.cadastrar(request, usuarioId(jwt));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<EventoResponse>> listarTodos(
            @RequestParam(required = false) Long usuarioId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long usuarioAutenticadoId = usuarioId(jwt);
        List<EventoResponse> eventos = usuarioId == null
                ? eventoService.listarTodos(usuarioAutenticadoId)
                : eventoService.listarPorUsuario(usuarioId, usuarioAutenticadoId);
        return ResponseEntity.ok(eventos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponse> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(eventoService.buscarPorId(id, usuarioId(jwt)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EventoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody EventoUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                eventoService.atualizar(id, request, usuarioId(jwt))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        eventoService.deletar(id, usuarioId(jwt));

        return ResponseEntity.noContent().build();
    }

    private Long usuarioId(Jwt jwt) {
        try {
            return Long.valueOf(jwt.getSubject());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Token não possui um identificador de usuário válido", exception);
        }
    }
}
