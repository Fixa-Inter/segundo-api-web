package com.exemplo.website.dto.aptidao;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record AptidaoUpdateRequest(
        @DecimalMin(value = "0.00", message = "A nota deve ser maior ou igual a zero")
        @DecimalMax(value = "10.00", message = "A nota deve ser menor ou igual a dez")
        BigDecimal nota,
        Boolean estaAtivo
){
}
