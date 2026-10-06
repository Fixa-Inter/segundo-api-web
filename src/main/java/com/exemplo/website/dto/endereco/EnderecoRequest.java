package com.exemplo.website.dto.endereco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EnderecoRequest(

        @NotNull(message = "A instituição é obrigatória")
        Long instituicaoId,

        @NotBlank(message = "O logradouro é obrigatório")
        String logradouro,

        @NotBlank(message = "O número é obrigatório")
        String numero,

        String complemento,

        @NotBlank(message = "O bairro é obrigatório")
        String bairro,

        @NotBlank(message = "A cidade é obrigatória")
        String cidade,

        @NotBlank(message = "O estado é obrigatório")
        @Size(min = 2, max = 2, message = "O estado deve possuir 2 caracteres")
        String estado,

        String pais,

        @NotBlank(message = "O CEP é obrigatório")
        String cep,

        @NotBlank(message = "O CNPJ é obrigatório")
        String cnpj
) {
}