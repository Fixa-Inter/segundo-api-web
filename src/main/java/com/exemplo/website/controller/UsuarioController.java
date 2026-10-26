package com.exemplo.website.controller;

import com.exemplo.website.dto.usuario.UsuarioRequest;
import com.exemplo.website.dto.usuario.UsuarioResponse;
import com.exemplo.website.dto.usuario.UsuarioUpdateRequest;
import com.exemplo.website.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestParam;
import com.exemplo.website.model.Enum.TipoAcesso;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('GESTOR', 'ADMINISTRADOR')")
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(
            UsuarioService usuarioService
    ) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(
            @Valid @RequestBody UsuarioRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {

        UsuarioResponse response =
                usuarioService.cadastrar(request, extrairUsuarioId(jwt));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>>
    listarTodos(
            @RequestParam(required = false) Long unidadeId,
            @RequestParam(required = false) TipoAcesso tipoAcesso,
            @AuthenticationPrincipal Jwt jwt
    ) {

        return ResponseEntity.ok(
                usuarioService.listarTodos(extrairUsuarioId(jwt), unidadeId, tipoAcesso)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {

        return ResponseEntity.ok(
                usuarioService.buscarPorId(id, extrairUsuarioId(jwt))
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {

        return ResponseEntity.ok(
                usuarioService.atualizar(id, request, extrairUsuarioId(jwt))
        );
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<UsuarioResponse> desativar(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {

        return ResponseEntity.ok(
                usuarioService.desativar(id, extrairUsuarioId(jwt))
        );
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<UsuarioResponse> ativar(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {

        return ResponseEntity.ok(
                usuarioService.ativar(id, extrairUsuarioId(jwt))
        );
    }

    private Long extrairUsuarioId(Jwt jwt) {
        try {
            return Long.valueOf(jwt.getClaimAsString("userId"));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Token não possui um identificador de usuário válido", exception);
        }
    }
}
