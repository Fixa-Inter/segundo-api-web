package com.exemplo.website.infrastructure.swagger;

import java.util.List;

final class AuthSwagger {
    private AuthSwagger() {
    }

    static List<SwaggerEndpoint> endpoints() {
        return List.of(
                new SwaggerEndpoint("POST", "/auth/login", "Autenticação", "Entrar", "Autentica por e-mail, senha e CNPJ e retorna access token e refresh token."),
                new SwaggerEndpoint("POST", "/auth/refresh", "Autenticação", "Renovar tokens", "Consome o refresh token atual e emite um novo par de tokens."),
                new SwaggerEndpoint("POST", "/auth/logout", "Autenticação", "Encerrar sessão", "Revoga o refresh token enviado."),
                new SwaggerEndpoint("POST", "/auth/alterar-senha", "Autenticação", "Alterar senha", "Altera a senha do usuário autenticado e revoga seus refresh tokens."),
                new SwaggerEndpoint("GET", "/auth/me", "Autenticação", "Consultar sessão", "Retorna o identificador e os perfis associados ao JWT atual.")
        );
    }
}
