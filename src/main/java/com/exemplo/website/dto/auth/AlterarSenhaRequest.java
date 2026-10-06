package com.exemplo.website.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaRequest(

        @NotBlank(message = "A senha atual é obrigatória")
        String senhaAtual,

        @NotBlank(message = "A nova senha é obrigatória")
        @Size(min = 8, message = "A nova senha deve possuir pelo menos 8 caracteres")
        String novaSenha,

        @NotBlank(message = "A confirmação da senha é obrigatória")
        String confirmarNovaSenha
) {
}