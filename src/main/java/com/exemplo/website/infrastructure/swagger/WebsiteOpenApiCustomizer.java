package com.exemplo.website.infrastructure.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class WebsiteOpenApiCustomizer implements OpenApiCustomizer {

    private static final Map<String, String> TAG_DESCRIPTIONS = Map.ofEntries(
            Map.entry("Autenticação", "Login, renovação de sessão e operações de senha."),
            Map.entry("Recuperação de senha", "Solicitação por e-mail e confirmação com código de recuperação."),
            Map.entry("Aptidões", "Aptidões de usuários por categoria de problema."),
            Map.entry("Eventos", "Registro e consulta de eventos vinculados a usuários e locais."),
            Map.entry("Instituições", "Cadastro e administração de instituições."),
            Map.entry("Foto de perfil", "Consulta, atualização e remoção da própria foto de perfil."),
            Map.entry("Locais", "Locais cadastrados dentro das unidades."),
            Map.entry("Perfil", "Consulta e edição do perfil do usuário autenticado."),
            Map.entry("Turnos", "Cadastro, consulta e ativação de turnos."),
            Map.entry("Vínculos de turnos", "Vínculo de usuários a turnos e consultas relacionadas."),
            Map.entry("Unidades", "Cadastro e administração de unidades e seus endereços."),
            Map.entry("Usuários", "Cadastro e administração de usuários conforme perfil e unidade.")
    );

    @Override
    public void customise(OpenAPI openAPI) {
        if (openAPI.getPaths() == null) {
            return;
        }

        Map<String, Tag> tags = new LinkedHashMap<>();
        TAG_DESCRIPTIONS.forEach((name, description) ->
                tags.put(name, new Tag().name(name).description(description)));
        openAPI.setTags(List.copyOf(tags.values()));

        WebsiteSwaggerCatalog.endpoints().forEach(endpoint -> {
            PathItem pathItem = openAPI.getPaths().get(endpoint.path());
            if (pathItem == null) {
                return;
            }

            Operation operation = operationFor(pathItem, endpoint.method());
            if (operation == null) {
                return;
            }

            operation.setTags(List.of(endpoint.tag()));
            operation.setSummary(endpoint.summary());
            operation.setDescription(endpoint.description());

            if (isSecured(endpoint.path())) {
                operation.setSecurity(List.of(new SecurityRequirement().addList("bearerAuth")));
            } else {
                operation.setSecurity(List.of());
            }
        });
    }

    private Operation operationFor(PathItem pathItem, String method) {
        return switch (method) {
            case "GET" -> pathItem.getGet();
            case "POST" -> pathItem.getPost();
            case "PUT" -> pathItem.getPut();
            case "PATCH" -> pathItem.getPatch();
            case "DELETE" -> pathItem.getDelete();
            default -> null;
        };
    }

    private boolean isSecured(String path) {
        return path.startsWith("/api/v1/")
                || path.equals("/auth/alterar-senha")
                || path.equals("/auth/me");
    }
}
