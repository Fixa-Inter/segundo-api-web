package com.exemplo.website.dto.turno;

public record TurnoUpdateRequest(
        Long enderecoId,
        String nome,
        Integer horaInicio,
        Integer horaFim,
        Boolean atravessaMeiaNoite
) {
}