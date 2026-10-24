package com.exemplo.website.infrastructure.swagger;

import java.util.List;

final class FotoSwagger {
    private FotoSwagger() {
    }

    static List<SwaggerEndpoint> endpoints() {
        return List.of(
                new SwaggerEndpoint("GET", "/api/v1/perfil/foto", "Foto de perfil", "Consultar minha foto", "Retorna a foto do usuário autenticado."),
                new SwaggerEndpoint("PUT", "/api/v1/perfil/foto", "Foto de perfil", "Salvar ou atualizar minha foto", "Salva ou substitui a foto do usuário autenticado."),
                new SwaggerEndpoint("DELETE", "/api/v1/perfil/foto", "Foto de perfil", "Remover minha foto", "Remove a foto do usuário autenticado.")
        );
    }
}
