package com.exemplo.website.dto.localendereco;

import com.exemplo.website.model.Enum.TipoLocalEndereco;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LocalEnderecoRequest(

        @NotNull(message = "O endereço é obrigatório")
        Long enderecoId,

        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotNull(message = "O tipo do local é obrigatório")
        TipoLocalEndereco tipoLocalEndereco,

        @NotBlank(message = "A descrição é obrigatória")
        String descricao
) {
}