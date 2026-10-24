package com.exemplo.website.dto.unidade;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UnidadeRequest(

        @NotNull(message = "A instituição é obrigatória")
        Long instituicaoId,

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve possuir no máximo 100 caracteres")
        String nome,

        @NotBlank(message = "O logradouro é obrigatório")
        @Size(max = 100, message = "O logradouro deve possuir no máximo 100 caracteres")
        String logradouro,

        @NotBlank(message = "O número é obrigatório")
        @Size(max = 10, message = "O número deve possuir no máximo 10 caracteres")
        String numero,

        @Size(max = 100, message = "O complemento deve possuir no máximo 100 caracteres")
        String complemento,

        @NotBlank(message = "O bairro é obrigatório")
        @Size(max = 100, message = "O bairro deve possuir no máximo 100 caracteres")
        String bairro,

        @NotBlank(message = "A cidade é obrigatória")
        @Size(max = 100, message = "A cidade deve possuir no máximo 100 caracteres")
        String cidade,

        @NotBlank(message = "O estado é obrigatório")
        @Size(min = 2, max = 2, message = "O estado deve possuir 2 caracteres")
        String estado,

        @NotBlank(message = "O CEP é obrigatório")
        @Pattern(regexp = "(?:[0-9]{8}|[0-9]{5}-[0-9]{3})", message = "O CEP deve possuir 8 dígitos")
        String cep,

        @NotBlank(message = "O CNPJ é obrigatório")
        @Pattern(regexp = "(?:[0-9]{14}|[0-9]{2}\\.[0-9]{3}\\.[0-9]{3}/[0-9]{4}-[0-9]{2})", message = "O CNPJ deve possuir 14 dígitos")
        String cnpj
) {
}
