package com.exemplo.website.controller;

import com.exemplo.website.dto.localendereco.LocalEnderecoRequest;
import com.exemplo.website.dto.localendereco.LocalEnderecoResponse;
import com.exemplo.website.dto.localendereco.LocalEnderecoUpdateRequest;
import com.exemplo.website.service.LocalEnderecoService;
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
@RequestMapping("/api/v1/locais")
public class LocalEnderecoController {

    private final LocalEnderecoService localEnderecoService;

    public LocalEnderecoController(
            LocalEnderecoService localEnderecoService
    ) {
        this.localEnderecoService = localEnderecoService;
    }

    @PostMapping
    public ResponseEntity<LocalEnderecoResponse> cadastrar(
            @Valid @RequestBody LocalEnderecoRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        localEnderecoService.cadastrar(request, usuarioId(jwt))
                );
    }

    @GetMapping
    public ResponseEntity<List<LocalEnderecoResponse>>
    listarTodos(
            @RequestParam(required = false) Long unidadeId,
            @AuthenticationPrincipal Jwt jwt
    ) {

        Long usuarioAutenticadoId = usuarioId(jwt);
        if (unidadeId == null) {
            return ResponseEntity.ok(localEnderecoService.listarTodos(usuarioAutenticadoId));
        }
        return ResponseEntity.ok(localEnderecoService.listarPorUnidade(unidadeId, usuarioAutenticadoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocalEnderecoResponse> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {

        return ResponseEntity.ok(
                localEnderecoService.buscarPorId(id, usuarioId(jwt))
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LocalEnderecoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody LocalEnderecoUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {

        return ResponseEntity.ok(
                localEnderecoService.atualizar(id, request, usuarioId(jwt))
        );
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<LocalEnderecoResponse> ativar(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {

        return ResponseEntity.ok(
                localEnderecoService.ativar(id, usuarioId(jwt))
        );
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<LocalEnderecoResponse> desativar(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {

        return ResponseEntity.ok(
                localEnderecoService.desativar(id, usuarioId(jwt))
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
