package com.exemplo.website.dto.usuario;

import com.exemplo.website.model.Enum.TipoAcesso;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UsuarioUpdateRequest(
        Long unidadeId,
        @Size(min = 3, max = 100, message = "O nome completo deve ter entre 3 e 100 caracteres")
        String nomeCompleto,
        @Email(message = "O email informado é inválido")
        @Size(max = 100, message = "O email deve possuir no máximo 100 caracteres")
        String email,
        TipoAcesso tipoAcesso,
        @Size(min = 2, max = 100, message = "O cargo deve ter entre 2 e 100 caracteres")
        String cargo,
        @Past(message = "A data de nascimento deve estar no passado")
        LocalDate dataNascimento
) {
}
