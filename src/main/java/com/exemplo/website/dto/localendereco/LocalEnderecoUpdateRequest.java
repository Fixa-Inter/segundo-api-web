package com.exemplo.website.dto.localendereco;

import com.exemplo.website.model.Enum.TipoLocalEndereco;

public record LocalEnderecoUpdateRequest(
        Long enderecoId,
        String nome,
        TipoLocalEndereco tipoLocalEndereco,
        String descricao
) {
}