package com.exemplo.website.dto.turno;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TurnoUpdateRequest(
        Long unidadeId,
        @Size(max = 100, message = "O nome do turno deve possuir no máximo 100 caracteres")
        @Pattern(regexp = ".*\\S.*", message = "O nome do turno não pode ficar em branco")
        String nome,
        Integer horaInicio,
        Integer horaFim,
        Boolean atravessaMeiaNoite
) {
}
