package com.exemplo.website.controller;

import com.exemplo.website.dto.turno.TurnoRequest;
import com.exemplo.website.dto.turno.TurnoResponse;
import com.exemplo.website.dto.turno.TurnoUpdateRequest;
import com.exemplo.website.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@PreAuthorize("hasAnyRole('GESTOR', 'ADMINISTRADOR')")
@RequestMapping("/api/v1/turnos")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @PostMapping
    public ResponseEntity<TurnoResponse> cadastrar(
            @Valid @RequestBody TurnoRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(turnoService.cadastrar(request, usuarioId(jwt)));
    }

    @GetMapping
    public ResponseEntity<List<TurnoResponse>> listar(
            @RequestParam(required = false) Long unidadeId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long usuarioAutenticadoId = usuarioId(jwt);
        List<TurnoResponse> turnos = unidadeId == null
                ? turnoService.listarTodos(usuarioAutenticadoId)
                : turnoService.listarPorUnidade(unidadeId, usuarioAutenticadoId);

        return ResponseEntity.ok(turnos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurnoResponse> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                turnoService.buscarPorId(id, usuarioId(jwt))
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TurnoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody TurnoUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                turnoService.atualizar(id, request, usuarioId(jwt))
        );
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<TurnoResponse> ativar(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                turnoService.ativar(id, usuarioId(jwt))
        );
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<TurnoResponse> desativar(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                turnoService.desativar(id, usuarioId(jwt))
        );
    }

    private Long usuarioId(Jwt jwt) {
        try {
            return Long.valueOf(jwt.getSubject());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Token não possui um identificador de usuário válido", exception);
        }
    }
}
