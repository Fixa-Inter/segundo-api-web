package com.exemplo.website.dto.usuario;

import com.exemplo.website.model.Enum.TipoAcesso;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UsuarioRequest(

        @NotNull(message = "O endereço é obrigatório")
        Long enderecoId,

        @NotBlank(message = "O nome completo é obrigatório")
        @Size(
                min = 3,
                max = 150,
                message = "O nome completo deve ter entre 3 e 150 caracteres"
        )
        String nomeCompleto,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "O email informado é inválido")
        @Size(
                max = 150,
                message = "O email deve possuir no máximo 150 caracteres"
        )
        String email,

        @NotNull(message = "O tipo de acesso é obrigatório")
        TipoAcesso tipoAcesso,

        @NotBlank(message = "O cargo é obrigatório")
        @Size(
                min = 2,
                max = 100,
                message = "O cargo deve ter entre 2 e 100 caracteres"
        )
        String cargo,

        @NotBlank(message = "A senha é obrigatória")
        @Size(
                min = 8,
                max = 100,
                message = "A senha deve ter entre 8 e 100 caracteres"
        )
        String senha,

        @NotNull(message = "A data de nascimento é obrigatória")
        @Past(message = "A data de nascimento deve estar no passado")
        LocalDate dataNascimento

) {
}