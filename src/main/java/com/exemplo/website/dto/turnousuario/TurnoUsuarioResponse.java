package com.exemplo.website.dto.turnousuario;

import java.time.LocalDateTime;

public record TurnoUsuarioResponse(
        Long id,
        Long turnoId,
        String turnoNome,
        Long usuarioId,
        String usuarioNome,
        Long unidadeId,
        LocalDateTime dataCriacao,
        Boolean estaAtivo
) {
}
