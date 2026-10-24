package com.exemplo.website.dto.instituicao;

import com.exemplo.website.model.Enum.TipoInstituicao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InstituicaoRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve possuir no máximo 100 caracteres")
        String nome,

        @NotNull(message = "O tipo da instituição é obrigatório")
        TipoInstituicao tipoInstituicao,

        @NotBlank(message = "O domínio de email é obrigatório")
        @Size(max = 100, message = "O domínio de email deve possuir no máximo 100 caracteres")
        String dominioEmail
) {
}
