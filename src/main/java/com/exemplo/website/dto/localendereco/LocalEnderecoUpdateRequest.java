package com.exemplo.website.dto.localendereco;

import com.exemplo.website.model.Enum.TipoLocalEndereco;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LocalEnderecoUpdateRequest(
        Long unidadeId,
        @Size(max = 100, message = "O nome deve possuir no máximo 100 caracteres")
        @Pattern(regexp = ".*\\S.*", message = "O nome não pode ficar em branco")
        String nome,
        TipoLocalEndereco tipoLocalEndereco,
        @Size(max = 255, message = "A descrição deve possuir no máximo 255 caracteres")
        @Pattern(regexp = ".*\\S.*", message = "A descrição não pode ficar em branco")
        String descricao
) {
}
