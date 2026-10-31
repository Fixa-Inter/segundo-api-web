package com.exemplo.website.infrastructure.swagger;

import java.util.List;

final class DashboardSwagger {
    private DashboardSwagger() {
    }

    static List<SwaggerEndpoint> endpoints() {
        return List.of(
                new SwaggerEndpoint("GET", "/api/v1/dashboard/embed", "Dashboard",
                        "Gerar acesso ao dashboard",
                        "Retorna um token curto do Databricks, restrito à unidade do usuário autenticado.")
        );
    }
}