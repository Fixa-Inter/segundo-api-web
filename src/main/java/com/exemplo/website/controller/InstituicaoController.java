package com.exemplo.website.controller;

import com.exemplo.website.dto.instituicao.InstituicaoRequest;
import com.exemplo.website.dto.instituicao.InstituicaoResponse;
import com.exemplo.website.dto.instituicao.InstituicaoUpdateRequest;
import com.exemplo.website.service.InstituicaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMINISTRADOR')")
@RequestMapping("/api/v1/instituicoes")
public class InstituicaoController {

    private final InstituicaoService instituicaoService;

    public InstituicaoController(
            InstituicaoService instituicaoService
    ) {
        this.instituicaoService = instituicaoService;
    }

    @PostMapping
    public ResponseEntity<InstituicaoResponse> cadastrar(
            @Valid @RequestBody InstituicaoRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(instituicaoService.cadastrar(request));
    }

    @GetMapping
    public ResponseEntity<List<InstituicaoResponse>> listarTodos() {
        return ResponseEntity.ok(
                instituicaoService.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstituicaoResponse> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                instituicaoService.buscarPorId(id)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<InstituicaoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody InstituicaoUpdateRequest request
    ) {
        return ResponseEntity.ok(
                instituicaoService.atualizar(id, request)
        );
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<InstituicaoResponse> ativar(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                instituicaoService.ativar(id)
        );
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<InstituicaoResponse> desativar(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                instituicaoService.desativar(id)
        );
    }
}
