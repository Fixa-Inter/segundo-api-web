package com.exemplo.website.infrastructure.swagger;

import java.util.List;

final class AptidaoSwagger {
    private AptidaoSwagger() {
    }

    static List<SwaggerEndpoint> endpoints() {
        return List.of(
                new SwaggerEndpoint("POST", "/api/v1/aptidoes", "Aptidões", "Cadastrar aptidão", "A nota aceita valores de 0 a 10 e a categoria deve ser única por usuário."),
                new SwaggerEndpoint("GET", "/api/v1/aptidoes", "Aptidões", "Listar aptidões por usuário", "Lista as aptidões do usuário informado."),
                new SwaggerEndpoint("GET", "/api/v1/aptidoes/{id}", "Aptidões", "Buscar aptidão por ID", "Consulta uma aptidão pelo identificador."),
                new SwaggerEndpoint("PATCH", "/api/v1/aptidoes/{id}", "Aptidões", "Atualizar aptidão", "Atualiza os dados da aptidão."),
                new SwaggerEndpoint("PATCH", "/api/v1/aptidoes/{id}/ativar", "Aptidões", "Ativar aptidão", "Ativa uma aptidão."),
                new SwaggerEndpoint("PATCH", "/api/v1/aptidoes/{id}/desativar", "Aptidões", "Desativar aptidão", "Desativa uma aptidão.")
        );
    }
}
