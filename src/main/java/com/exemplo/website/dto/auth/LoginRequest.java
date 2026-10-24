package com.exemplo.website.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "O email informado é inválido")
        @Size(max = 100, message = "O email deve possuir no máximo 100 caracteres")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        String senha,

        @NotBlank(message = "O CNPJ é obrigatório")
        String cnpj
) {
}
