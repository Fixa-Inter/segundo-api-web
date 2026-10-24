package com.exemplo.website.infrastructure.swagger;

import java.util.List;

final class EventoSwagger {
    private EventoSwagger() {
    }

    static List<SwaggerEndpoint> endpoints() {
        return List.of(
                new SwaggerEndpoint("POST", "/api/v1/eventos", "Eventos", "Cadastrar evento", "Usuário e local devem pertencer à mesma unidade e o horário final deve ser posterior ao inicial."),
                new SwaggerEndpoint("GET", "/api/v1/eventos", "Eventos", "Listar eventos", "Pode filtrar os eventos por usuário; gestores ficam restritos à própria unidade."),
                new SwaggerEndpoint("GET", "/api/v1/eventos/{id}", "Eventos", "Buscar evento por ID", "Consulta um evento pelo identificador."),
                new SwaggerEndpoint("PATCH", "/api/v1/eventos/{id}", "Eventos", "Atualizar evento", "Atualiza os dados do evento."),
                new SwaggerEndpoint("DELETE", "/api/v1/eventos/{id}", "Eventos", "Excluir evento", "Remove um evento.")
        );
    }
}
