package com.exemplo.website.dto.perfil;

import java.time.LocalDate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record PerfilUpdateRequest(
        @Size(min = 3, max = 100, message = "O nome completo deve ter entre 3 e 100 caracteres")
        String nomeCompleto,
        @Email(message = "O email informado é inválido")
        @Size(max = 100, message = "O email deve possuir no máximo 100 caracteres")
        String email,
        @Past(message = "A data de nascimento deve estar no passado")
        LocalDate dataNascimento
) {
}
