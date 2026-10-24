package com.exemplo.website.dto.turno;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TurnoRequest(

        @NotNull(message = "A unidade é obrigatória")
        Long unidadeId,

        @NotBlank(message = "O nome do turno é obrigatório")
        @Size(max = 100, message = "O nome do turno deve possuir no máximo 100 caracteres")
        String nome,

        @NotNull(message = "A hora de início é obrigatória")
        @Min(value = 0, message = "A hora de início deve ser informada em segundos entre 0 e 86400")
        @Max(value = 86400, message = "A hora de início deve ser informada em segundos entre 0 e 86400")
        Integer horaInicio,

        @NotNull(message = "A hora de fim é obrigatória")
        @Min(value = 0, message = "A hora de fim deve ser informada em segundos entre 0 e 86400")
        @Max(value = 86400, message = "A hora de fim deve ser informada em segundos entre 0 e 86400")
        Integer horaFim,

        @NotNull(message = "Informe se o turno atravessa a meia-noite")
        Boolean atravessaMeiaNoite
) {
}
