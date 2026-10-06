package com.exemplo.website.dto.instituicao;

import com.exemplo.website.model.Enum.TipoInstituicao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InstituicaoRequest(

        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotNull(message = "O tipo da instituição é obrigatório")
        TipoInstituicao tipoInstituicao,

        @NotBlank(message = "O domínio de email é obrigatório")
        String dominioEmail
) {
}