package com.exemplo.website.dto.instituicao;

import com.exemplo.website.model.Enum.TipoInstituicao;

public record InstituicaoUpdateRequest(
        String nome,
        TipoInstituicao tipoInstituicao,
        String dominioEmail
) {
}