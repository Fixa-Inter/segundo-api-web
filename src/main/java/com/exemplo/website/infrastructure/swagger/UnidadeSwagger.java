package com.exemplo.website.infrastructure.swagger;

import java.util.List;

final class UnidadeSwagger {
    private UnidadeSwagger() {
    }

    static List<SwaggerEndpoint> endpoints() {
        return List.of(
                new SwaggerEndpoint("POST", "/api/v1/unidades", "Unidades", "Cadastrar unidade", "Cadastra uma unidade."),
                new SwaggerEndpoint("GET", "/api/v1/unidades", "Unidades", "Listar unidades", "Lista as unidades cadastradas."),
                new SwaggerEndpoint("GET", "/api/v1/unidades/{id}", "Unidades", "Buscar unidade por ID", "Consulta uma unidade pelo identificador."),
                new SwaggerEndpoint("PATCH", "/api/v1/unidades/{id}", "Unidades", "Atualizar unidade", "Atualiza os dados da unidade."),
                new SwaggerEndpoint("PATCH", "/api/v1/unidades/{id}/ativar", "Unidades", "Ativar unidade", "Ativa uma unidade."),
                new SwaggerEndpoint("PATCH", "/api/v1/unidades/{id}/desativar", "Unidades", "Desativar unidade", "Desativa uma unidade.")
        );
    }
}
