package com.exemplo.website.infrastructure.swagger;

import java.util.List;

final class InstituicaoSwagger {
    private InstituicaoSwagger() {
    }

    static List<SwaggerEndpoint> endpoints() {
        return List.of(
                new SwaggerEndpoint("POST", "/api/v1/instituicoes", "Instituições", "Cadastrar instituição", "Cadastra uma instituição."),
                new SwaggerEndpoint("GET", "/api/v1/instituicoes", "Instituições", "Listar instituições", "Lista as instituições cadastradas."),
                new SwaggerEndpoint("GET", "/api/v1/instituicoes/{id}", "Instituições", "Buscar instituição por ID", "Consulta uma instituição pelo identificador."),
                new SwaggerEndpoint("PATCH", "/api/v1/instituicoes/{id}", "Instituições", "Atualizar instituição", "Atualiza os dados da instituição."),
                new SwaggerEndpoint("PATCH", "/api/v1/instituicoes/{id}/ativar", "Instituições", "Ativar instituição", "Ativa uma instituição."),
                new SwaggerEndpoint("PATCH", "/api/v1/instituicoes/{id}/desativar", "Instituições", "Desativar instituição", "Desativa uma instituição.")
        );
    }
}
