package com.exemplo.website.dto.instituicao;

import com.exemplo.website.model.Enum.TipoInstituicao;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InstituicaoUpdateRequest(
        @Size(max = 100, message = "O nome deve possuir no máximo 100 caracteres")
        @Pattern(regexp = ".*\\S.*", message = "O nome não pode ficar em branco")
        String nome,
        TipoInstituicao tipoInstituicao,
        @Size(max = 100, message = "O domínio de email deve possuir no máximo 100 caracteres")
        @Pattern(regexp = ".*\\S.*", message = "O domínio de email não pode ficar em branco")
        String dominioEmail
) {
}
