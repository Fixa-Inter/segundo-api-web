package com.exemplo.website.dto.endereco;

import java.time.LocalDateTime;

public record EnderecoResponse(
        Long id,
        Long instituicaoId,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String pais,
        String cep,
        String cnpj,
        LocalDateTime dataCriacao,
        Boolean estaAtivo
) {
}