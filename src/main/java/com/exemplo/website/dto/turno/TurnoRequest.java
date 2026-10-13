package com.exemplo.website.dto.turno;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record TurnoRequest(

        @NotNull(message = "O endereço é obrigatório")
        Long enderecoId,

        @NotBlank(message = "O nome do turno é obrigatório")
        String nome,

        @NotNull(message = "A hora de início é obrigatória")
        LocalTime horaInicio,

        @NotNull(message = "A hora de fim é obrigatória")
        LocalTime horaFim,

        @NotNull(message = "Informe se o turno atravessa a meia-noite")
        Boolean atravessaMeiaNoite
) {
}
