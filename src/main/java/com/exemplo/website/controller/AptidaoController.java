package com.exemplo.website.controller;

import com.exemplo.website.dto.aptidao.AptidaoRequest;
import com.exemplo.website.dto.aptidao.AptidaoResponse;
import com.exemplo.website.dto.aptidao.AptidaoUpdateRequest;
import com.exemplo.website.service.AptidaoService;
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
@RequestMapping("/api/v1/aptidoes")
public class AptidaoController {

    private final AptidaoService aptidaoService;

    public AptidaoController(AptidaoService aptidaoService) {
        this.aptidaoService = aptidaoService;
    }

    @PostMapping
    public ResponseEntity<AptidaoResponse> cadastrar(
            @Valid @RequestBody AptidaoRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(aptidaoService.cadastrar(request, usuarioId(jwt)));
    }

    @GetMapping
    public ResponseEntity<List<AptidaoResponse>> listarPorUsuario(
            @RequestParam Long usuarioId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                aptidaoService.listarPorUsuario(usuarioId, usuarioId(jwt))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AptidaoResponse> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                aptidaoService.buscarPorId(id, usuarioId(jwt))
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AptidaoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AptidaoUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                aptidaoService.atualizar(id, request, usuarioId(jwt))
        );
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<AptidaoResponse> ativar(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                aptidaoService.ativar(id, usuarioId(jwt))
        );
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<AptidaoResponse> desativar(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                aptidaoService.desativar(id, usuarioId(jwt))
        );
    }

    private Long usuarioId(Jwt jwt) {
        try {
            return Long.valueOf(jwt.getClaimAsString("userId"));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Token não possui um identificador de usuário válido", exception);
        }
    }
}
