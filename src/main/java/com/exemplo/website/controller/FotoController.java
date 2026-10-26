package com.exemplo.website.controller;

import com.exemplo.website.dto.foto.FotoRequest;
import com.exemplo.website.dto.foto.FotoResponse;
import com.exemplo.website.service.FotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/v1/perfil/foto")
@RequiredArgsConstructor
public class FotoController {

    private final FotoService fotoService;

    @GetMapping
    public ResponseEntity<FotoResponse> buscar(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                fotoService.buscarPorUsuarioId(extrairUsuarioId(jwt))
        );
    }

    @PutMapping
    public ResponseEntity<FotoResponse> salvarOuAtualizar(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody FotoRequest request
    ) {
        return ResponseEntity.ok(
                fotoService.salvarOuAtualizar(
                        extrairUsuarioId(jwt),
                        request
                )
        );
    }

    @DeleteMapping
    public ResponseEntity<Void> remover(
            @AuthenticationPrincipal Jwt jwt
    ) {
        fotoService.removerPorUsuarioId(extrairUsuarioId(jwt));
        return ResponseEntity.noContent().build();
    }

    private Long extrairUsuarioId(Jwt jwt) {
        try {
            return Long.valueOf(jwt.getClaimAsString("userId"));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Token não possui um identificador de usuário válido",
                    exception
            );
        }
    }
}
