package com.exemplo.website.dto.localendereco;

import com.exemplo.website.model.Enum.TipoLocalEndereco;

import java.time.LocalDateTime;

public record LocalEnderecoResponse(
        Long id,
        Long enderecoId,
        String nome,
        TipoLocalEndereco tipoLocalEndereco,
        String descricao,
        LocalDateTime dataCriacao,
        Boolean estaAtivo
) {
}