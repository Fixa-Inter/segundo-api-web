package com.exemplo.website.controller;

import com.exemplo.website.dto.unidade.UnidadeRequest;
import com.exemplo.website.dto.unidade.UnidadeResponse;
import com.exemplo.website.dto.unidade.UnidadeUpdateRequest;
import com.exemplo.website.service.UnidadeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMINISTRADOR')")
@RequestMapping("/api/v1/unidades")
public class UnidadeController {

    private final UnidadeService unidadeService;

    public UnidadeController(
            UnidadeService unidadeService
    ) {
        this.unidadeService = unidadeService;
    }

    @PostMapping
    public ResponseEntity<UnidadeResponse> cadastrar(
            @Valid @RequestBody UnidadeRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        unidadeService.cadastrar(request)
                );
    }

    @GetMapping
    public ResponseEntity<List<UnidadeResponse>>
    listarTodos() {

        return ResponseEntity.ok(
                unidadeService.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnidadeResponse> buscarPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                unidadeService.buscarPorId(id)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UnidadeResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UnidadeUpdateRequest request
    ) {

        return ResponseEntity.ok(
                unidadeService.atualizar(id, request)
        );
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<UnidadeResponse> ativar(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                unidadeService.ativar(id)
        );
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<UnidadeResponse> desativar(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                unidadeService.desativar(id)
        );
    }
}
