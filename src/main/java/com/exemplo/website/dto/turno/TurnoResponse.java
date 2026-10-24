package com.exemplo.website.dto.turno;

import java.time.LocalDateTime;

public record TurnoResponse(
        Long id,
        Long unidadeId,
        String nome,
        Integer horaInicio,
        Integer horaFim,
        Boolean atravessaMeiaNoite,
        LocalDateTime dataCriacao,
        Boolean estaAtivo
) {
}
