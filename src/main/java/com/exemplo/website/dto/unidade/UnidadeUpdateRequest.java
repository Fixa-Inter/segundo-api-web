package com.exemplo.website.dto.unidade;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UnidadeUpdateRequest(
        Long instituicaoId,
        @Size(max = 100, message = "O nome deve possuir no máximo 100 caracteres")
        @Pattern(regexp = ".*\\S.*", message = "O nome não pode ficar em branco")
        String nome,
        @Size(max = 100, message = "O logradouro deve possuir no máximo 100 caracteres")
        @Pattern(regexp = ".*\\S.*", message = "O logradouro não pode ficar em branco")
        String logradouro,
        @Size(max = 10, message = "O número deve possuir no máximo 10 caracteres")
        @Pattern(regexp = ".*\\S.*", message = "O número não pode ficar em branco")
        String numero,
        @Size(max = 100, message = "O complemento deve possuir no máximo 100 caracteres")
        String complemento,
        @Size(max = 100, message = "O bairro deve possuir no máximo 100 caracteres")
        @Pattern(regexp = ".*\\S.*", message = "O bairro não pode ficar em branco")
        String bairro,
        @Size(max = 100, message = "A cidade deve possuir no máximo 100 caracteres")
        @Pattern(regexp = ".*\\S.*", message = "A cidade não pode ficar em branco")
        String cidade,
        @Pattern(regexp = "[A-Za-z]{2}", message = "O estado deve possuir 2 letras")
        String estado,
        @Pattern(regexp = "(?:[0-9]{8}|[0-9]{5}-[0-9]{3})", message = "O CEP deve possuir 8 dígitos")
        String cep,
        @Pattern(regexp = "(?:[0-9]{14}|[0-9]{2}\\.[0-9]{3}\\.[0-9]{3}/[0-9]{4}-[0-9]{2})", message = "O CNPJ deve possuir 14 dígitos")
        String cnpj
) {
}
