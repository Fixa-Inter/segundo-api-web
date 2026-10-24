package com.exemplo.website.controller;

import com.exemplo.website.dto.perfil.PerfilResponse;
import com.exemplo.website.dto.perfil.PerfilUpdateRequest;
import com.exemplo.website.service.PerfilService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/v1/perfil")
public class PerfilController {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping
    public ResponseEntity<PerfilResponse> buscarPerfil(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                perfilService.buscarPorUsuarioId(
                        extrairUsuarioId(jwt)
                )
        );
    }

    @PatchMapping
    public ResponseEntity<PerfilResponse> atualizarPerfil(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody PerfilUpdateRequest request
    ) {
        return ResponseEntity.ok(
                perfilService.atualizar(
                        extrairUsuarioId(jwt),
                        request
                )
        );
    }

    private Long extrairUsuarioId(Jwt jwt) {
        try {
            return Long.valueOf(jwt.getSubject());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Token não possui um identificador de usuário válido"
            );
        }
    }
}
