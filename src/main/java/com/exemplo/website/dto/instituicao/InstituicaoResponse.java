package com.exemplo.website.dto.instituicao;

import com.exemplo.website.model.Enum.TipoInstituicao;

import java.time.LocalDateTime;

public record InstituicaoResponse(
        Long id,
        String nome,
        TipoInstituicao tipoInstituicao,
        String dominioEmail,
        LocalDateTime dataCriacao,
        Boolean estaAtivo
) {
}