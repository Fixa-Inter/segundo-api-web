package com.exemplo.website.dto.unidade;

import java.time.LocalDateTime;

public record UnidadeResponse(
        Long id,
        Long instituicaoId,
        String nome,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep,
        String cnpj,
        LocalDateTime dataCriacao,
        Boolean estaAtivo
) {
}
