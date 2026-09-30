package com.exemplo.website.controller;

import com.exemplo.website.dto.usuario.UsuarioRequest;
import com.exemplo.website.dto.usuario.UsuarioResponse;
import com.exemplo.website.dto.usuario.UsuarioUpdateRequest;
import com.exemplo.website.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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
            @RequestBody UsuarioRequest request
    ) {

        UsuarioResponse response =
                usuarioService.cadastrar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>>
    listarTodos() {

        return ResponseEntity.ok(
                usuarioService.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                usuarioService.buscarPorId(id)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizar(
            @PathVariable Long id,
            @RequestBody UsuarioUpdateRequest request
    ) {

        return ResponseEntity.ok(
                usuarioService.atualizar(id, request)
        );
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<UsuarioResponse> desativar(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                usuarioService.desativar(id)
        );
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<UsuarioResponse> ativar(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                usuarioService.ativar(id)
        );
    }
}