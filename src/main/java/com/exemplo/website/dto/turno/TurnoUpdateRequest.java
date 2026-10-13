package com.exemplo.website.dto.turno;

import java.time.LocalTime;

public record TurnoUpdateRequest(
        Long enderecoId,
        String nome,
        LocalTime horaInicio,
        LocalTime horaFim,
        Boolean atravessaMeiaNoite
) {
}
