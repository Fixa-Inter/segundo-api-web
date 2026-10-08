package com.exemplo.website.controller;

import com.exemplo.website.dto.endereco.EnderecoRequest;
import com.exemplo.website.dto.endereco.EnderecoResponse;
import com.exemplo.website.dto.endereco.EnderecoUpdateRequest;
import com.exemplo.website.service.EnderecoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enderecos")
public class EnderecoController {

    private final EnderecoService enderecoService;

    public EnderecoController(
            EnderecoService enderecoService
    ) {
        this.enderecoService = enderecoService;
    }

    @PostMapping
    public ResponseEntity<EnderecoResponse> cadastrar(
            @RequestBody EnderecoRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        enderecoService.cadastrar(request)
                );
    }

    @GetMapping
    public ResponseEntity<List<EnderecoResponse>>
    listarTodos() {

        return ResponseEntity.ok(
                enderecoService.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnderecoResponse> buscarPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                enderecoService.buscarPorId(id)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EnderecoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody EnderecoUpdateRequest request
    ) {

        return ResponseEntity.ok(
                enderecoService.atualizar(id, request)
        );
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<EnderecoResponse> ativar(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                enderecoService.ativar(id)
        );
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<EnderecoResponse> desativar(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                enderecoService.desativar(id)
        );
    }
}