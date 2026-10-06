package com.exemplo.website.dto.turno;

import java.time.LocalDateTime;

public record TurnoResponse(
        Long id,
        Long enderecoId,
        String nome,
        Integer horaInicio,
        Integer horaFim,
        Boolean atravessaMeiaNoite,
        LocalDateTime dataCriacao,
        Boolean estaAtivo
) {
}