package com.exemplo.website.dto.localendereco;

import com.exemplo.website.model.Enum.TipoLocalEndereco;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LocalEnderecoRequest(

        @NotNull(message = "A unidade é obrigatória")
        Long unidadeId,

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve possuir no máximo 100 caracteres")
        String nome,

        @NotNull(message = "O tipo do local é obrigatório")
        TipoLocalEndereco tipoLocalEndereco,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 255, message = "A descrição deve possuir no máximo 255 caracteres")
        String descricao
) {
}
