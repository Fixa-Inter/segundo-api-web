package com.exemplo.website.controller;

import com.exemplo.website.dto.turnousuario.TurnoUsuarioResponse;
import com.exemplo.website.service.TurnoUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@PreAuthorize("hasAnyRole('GESTOR', 'ADMINISTRADOR')")
@RequiredArgsConstructor
public class TurnoUsuarioController {

    private final TurnoUsuarioService turnoUsuarioService;

    @PostMapping("/api/v1/turnos/{turnoId}/usuarios/{usuarioId}")
    public ResponseEntity<TurnoUsuarioResponse> vincular(
            @PathVariable Long turnoId,
            @PathVariable Long usuarioId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(turnoUsuarioService.vincular(turnoId, usuarioId, usuarioId(jwt)));
    }

    @GetMapping("/api/v1/turnos/{turnoId}/usuarios")
    public ResponseEntity<List<TurnoUsuarioResponse>> listarUsuariosDoTurno(
            @PathVariable Long turnoId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                turnoUsuarioService.listarUsuariosDoTurno(turnoId, usuarioId(jwt))
        );
    }

    @GetMapping("/api/v1/usuarios/{usuarioId}/turnos")
    public ResponseEntity<List<TurnoUsuarioResponse>> listarTurnosDoUsuario(
            @PathVariable Long usuarioId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                turnoUsuarioService.listarTurnosDoUsuario(usuarioId, usuarioId(jwt))
        );
    }

    @PatchMapping("/api/v1/turnos-usuarios/{id}/ativar")
    public ResponseEntity<TurnoUsuarioResponse> ativar(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(turnoUsuarioService.ativar(id, usuarioId(jwt)));
    }

    @PatchMapping("/api/v1/turnos-usuarios/{id}/desativar")
    public ResponseEntity<TurnoUsuarioResponse> desativar(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(turnoUsuarioService.desativar(id, usuarioId(jwt)));
    }

    private Long usuarioId(Jwt jwt) {
        try {
            return Long.valueOf(jwt.getSubject());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Token não possui um identificador de usuário válido", exception);
        }
    }
}
