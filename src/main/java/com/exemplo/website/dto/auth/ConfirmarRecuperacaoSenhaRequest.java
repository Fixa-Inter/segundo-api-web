package com.exemplo.website.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ConfirmarRecuperacaoSenhaRequest(
        @NotBlank(message = "O email é obrigatório")
        @Email(message = "O email informado é inválido")
        @Size(max = 100, message = "O email deve possuir no máximo 100 caracteres")
        String email,

        @NotNull(message = "O código é obrigatório")
        @Min(value = 100000, message = "O código deve possuir 6 dígitos")
        @Max(value = 999999, message = "O código deve possuir 6 dígitos")
        Integer codigo,

        @NotBlank(message = "A nova senha é obrigatória")
        @Size(min = 8, max = 100, message = "A senha deve ter entre 8 e 100 caracteres")
        String novaSenha
) {
}
