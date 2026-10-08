package com.exemplo.website.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RedefinirSenhaRequest(

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotNull(message = "O código é obrigatório")
        Integer codigo,

        @NotBlank(message = "A nova senha é obrigatória")
        @Size(min = 8, message = "A nova senha deve possuir pelo menos 8 caracteres")
        String novaSenha,

        @NotBlank(message = "A confirmação da senha é obrigatória")
        String confirmarNovaSenha
) {
}