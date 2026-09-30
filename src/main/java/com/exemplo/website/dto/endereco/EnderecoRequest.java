package com.exemplo.website.dto.endereco;

public record EnderecoRequest(
        Long instituicaoId,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String pais,
        String cep,
        String cnpj
) {
}