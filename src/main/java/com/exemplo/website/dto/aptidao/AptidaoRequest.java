package com.exemplo.website.dto.aptidao;

import com.exemplo.website.model.Enum.CategoriaProblema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record AptidaoRequest(
        @NotNull(message = "O usuário é obrigatório")
        Long usuarioId,

        @NotNull(message = "A categoria do problema é obrigatória")
        CategoriaProblema categoriaProblema,

        @NotNull(message = "A nota é obrigatória")
        @DecimalMin(value = "0.00", message = "A nota deve ser maior ou igual a zero")
        @DecimalMax(value = "10.00", message = "A nota deve ser menor ou igual a dez")
        Double nota
) {
}
