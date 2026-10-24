package com.exemplo.website.controller;

import com.exemplo.website.dto.auth.ConfirmarRecuperacaoSenhaRequest;
import com.exemplo.website.dto.auth.SolicitarRecuperacaoSenhaRequest;
import com.exemplo.website.service.RecuperacaoSenhaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth/recuperar-senha")
@RequiredArgsConstructor
public class RecuperacaoSenhaController {

    private final RecuperacaoSenhaService recuperacaoSenhaService;

    @PostMapping
    public ResponseEntity<Map<String, String>> solicitar(
            @Valid @RequestBody SolicitarRecuperacaoSenhaRequest request
    ) {
        recuperacaoSenhaService.solicitar(request);
        return ResponseEntity.accepted().body(Map.of(
                "mensagem",
                "Se o email estiver cadastrado, você receberá um código de recuperação."
        ));
    }

    @PostMapping("/confirmar")
    public ResponseEntity<Map<String, String>> confirmar(
            @Valid @RequestBody ConfirmarRecuperacaoSenhaRequest request
    ) {
        recuperacaoSenhaService.confirmar(request);
        return ResponseEntity.ok(Map.of(
                "mensagem",
                "Senha redefinida com sucesso."
        ));
    }
}
