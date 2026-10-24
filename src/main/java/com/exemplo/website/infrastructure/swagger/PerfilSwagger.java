package com.exemplo.website.infrastructure.swagger;

import java.util.List;

final class PerfilSwagger {
    private PerfilSwagger() {
    }

    static List<SwaggerEndpoint> endpoints() {
        return List.of(
                new SwaggerEndpoint("GET", "/api/v1/perfil", "Perfil", "Consultar meu perfil", "Retorna o perfil do usuário autenticado."),
                new SwaggerEndpoint("PATCH", "/api/v1/perfil", "Perfil", "Atualizar meu perfil", "Atualiza os dados do usuário autenticado.")
        );
    }
}
