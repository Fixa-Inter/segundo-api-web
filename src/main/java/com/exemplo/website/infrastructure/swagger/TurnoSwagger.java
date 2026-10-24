package com.exemplo.website.infrastructure.swagger;

import java.util.List;

final class TurnoSwagger {
    private TurnoSwagger() {
    }

    static List<SwaggerEndpoint> endpoints() {
        return List.of(
                new SwaggerEndpoint("POST", "/api/v1/turnos", "Turnos", "Cadastrar turno", "Cadastra um turno."),
                new SwaggerEndpoint("GET", "/api/v1/turnos", "Turnos", "Listar turnos", "Lista os turnos cadastrados."),
                new SwaggerEndpoint("GET", "/api/v1/turnos/{id}", "Turnos", "Buscar turno por ID", "Consulta um turno pelo identificador."),
                new SwaggerEndpoint("PATCH", "/api/v1/turnos/{id}", "Turnos", "Atualizar turno", "Atualiza os dados do turno."),
                new SwaggerEndpoint("PATCH", "/api/v1/turnos/{id}/ativar", "Turnos", "Ativar turno", "Ativa um turno."),
                new SwaggerEndpoint("PATCH", "/api/v1/turnos/{id}/desativar", "Turnos", "Desativar turno", "Desativa um turno.")
        );
    }
}
