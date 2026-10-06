package com.exemplo.website.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "Email ou CNPJ é obrigatório")
        String identificador,

        @NotBlank(message = "A senha é obrigatória")
        String senha
) {
}