package com.exemplo.website.infrastructure.swagger;

import java.util.List;

final class LocalEnderecoSwagger {
    private LocalEnderecoSwagger() {
    }

    static List<SwaggerEndpoint> endpoints() {
        return List.of(
                new SwaggerEndpoint("POST", "/api/v1/locais", "Locais", "Cadastrar local", "Cadastra um local em uma unidade."),
                new SwaggerEndpoint("GET", "/api/v1/locais", "Locais", "Listar locais", "Administradores podem listar tudo; gestores ficam limitados à própria unidade."),
                new SwaggerEndpoint("GET", "/api/v1/locais/{id}", "Locais", "Buscar local por ID", "Consulta um local pelo identificador."),
                new SwaggerEndpoint("PATCH", "/api/v1/locais/{id}", "Locais", "Atualizar local", "Atualiza os dados do local."),
                new SwaggerEndpoint("PATCH", "/api/v1/locais/{id}/ativar", "Locais", "Ativar local", "Ativa um local."),
                new SwaggerEndpoint("PATCH", "/api/v1/locais/{id}/desativar", "Locais", "Desativar local", "Desativa um local.")
        );
    }
}
