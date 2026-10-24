package com.exemplo.website.infrastructure.swagger;

import java.util.List;

final class RecuperacaoSenhaSwagger {
    private RecuperacaoSenhaSwagger() {
    }

    static List<SwaggerEndpoint> endpoints() {
        return List.of(
                new SwaggerEndpoint("POST", "/auth/recuperar-senha", "Recuperação de senha", "Solicitar código", "Envia um código temporário para o e-mail cadastrado."),
                new SwaggerEndpoint("POST", "/auth/recuperar-senha/confirmar", "Recuperação de senha", "Confirmar recuperação", "Valida o código e define uma nova senha.")
        );
    }
}
