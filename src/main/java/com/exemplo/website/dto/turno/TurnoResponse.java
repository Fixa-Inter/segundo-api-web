package com.exemplo.website.dto.turno;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record TurnoResponse(
        Long id,
        Long enderecoId,
        String nome,
        LocalTime horaInicio,
        LocalTime horaFim,
        Boolean atravessaMeiaNoite,
        LocalDateTime dataCriacao,
        Boolean estaAtivo
) {
}
