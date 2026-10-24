package com.exemplo.website.infrastructure.swagger;

import java.util.List;

final class TurnoUsuarioSwagger {
    private TurnoUsuarioSwagger() {
    }

    static List<SwaggerEndpoint> endpoints() {
        return List.of(
                new SwaggerEndpoint("POST", "/api/v1/turnos/{turnoId}/usuarios/{usuarioId}", "Vínculos de turnos", "Vincular usuário ao turno", "Vincula um usuário a um turno."),
                new SwaggerEndpoint("GET", "/api/v1/turnos/{turnoId}/usuarios", "Vínculos de turnos", "Listar usuários do turno", "Lista os usuários vinculados ao turno."),
                new SwaggerEndpoint("GET", "/api/v1/usuarios/{usuarioId}/turnos", "Vínculos de turnos", "Listar turnos do usuário", "Lista os turnos vinculados ao usuário."),
                new SwaggerEndpoint("PATCH", "/api/v1/turnos-usuarios/{id}/ativar", "Vínculos de turnos", "Ativar vínculo de turno", "Ativa o vínculo entre usuário e turno."),
                new SwaggerEndpoint("PATCH", "/api/v1/turnos-usuarios/{id}/desativar", "Vínculos de turnos", "Desativar vínculo de turno", "Desativa o vínculo entre usuário e turno.")
        );
    }
}
