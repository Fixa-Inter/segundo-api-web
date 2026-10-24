package com.exemplo.website.infrastructure.swagger;

import java.util.List;

final class UsuarioSwagger {
    private UsuarioSwagger() {
    }

    static List<SwaggerEndpoint> endpoints() {
        return List.of(
                new SwaggerEndpoint("POST", "/api/v1/usuarios", "Usuários", "Cadastrar usuário", "Cadastra um usuário."),
                new SwaggerEndpoint("GET", "/api/v1/usuarios", "Usuários", "Listar usuários", "Gestores veem somente técnicos da própria unidade; administradores podem filtrar por unidade e perfil."),
                new SwaggerEndpoint("GET", "/api/v1/usuarios/{id}", "Usuários", "Buscar usuário por ID", "Consulta um usuário pelo identificador."),
                new SwaggerEndpoint("PATCH", "/api/v1/usuarios/{id}", "Usuários", "Atualizar usuário", "Atualiza os dados do usuário."),
                new SwaggerEndpoint("PATCH", "/api/v1/usuarios/{id}/desativar", "Usuários", "Desativar usuário", "Desativa um usuário."),
                new SwaggerEndpoint("PATCH", "/api/v1/usuarios/{id}/ativar", "Usuários", "Ativar usuário", "Ativa um usuário.")
        );
    }
}
