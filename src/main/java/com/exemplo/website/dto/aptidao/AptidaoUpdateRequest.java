package com.exemplo.website.dto.aptidao;

import jakarta.validation.constraints.NotNull;

public record AptidaoUpdateRequest(
        @NotNull(message = "O ID é obrigatório!")
        Long id,
        Double nota,
        Boolean estaAtivo
){
}
