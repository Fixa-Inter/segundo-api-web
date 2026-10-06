package com.exemplo.website.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EsqueceuSenhaRequest(

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Email inválido")
        String email
) {
}