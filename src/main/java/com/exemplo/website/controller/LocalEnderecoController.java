package com.exemplo.website.controller;

import com.exemplo.website.dto.localendereco.LocalEnderecoRequest;
import com.exemplo.website.dto.localendereco.LocalEnderecoResponse;
import com.exemplo.website.dto.localendereco.LocalEnderecoUpdateRequest;
import com.exemplo.website.service.LocalEnderecoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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
            @RequestBody LocalEnderecoRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        localEnderecoService.cadastrar(request)
                );
    }

    @GetMapping
    public ResponseEntity<List<LocalEnderecoResponse>>
    listarTodos() {

        return ResponseEntity.ok(
                localEnderecoService.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocalEnderecoResponse> buscarPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                localEnderecoService.buscarPorId(id)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LocalEnderecoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody LocalEnderecoUpdateRequest request
    ) {

        return ResponseEntity.ok(
                localEnderecoService.atualizar(id, request)
        );
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<LocalEnderecoResponse> ativar(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                localEnderecoService.ativar(id)
        );
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<LocalEnderecoResponse> desativar(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                localEnderecoService.desativar(id)
        );
    }
}