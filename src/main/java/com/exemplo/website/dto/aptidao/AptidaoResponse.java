package com.exemplo.website.dto.aptidao;

import com.exemplo.website.model.Enum.CategoriaProblema;

import java.time.LocalDateTime;

public record AptidaoResponse(
        Long id,
        Long usuarioId,
        CategoriaProblema categoriaProblema,
        Double nota,
        LocalDateTime dataCriacao,
        Boolean estaAtivo
) {
}
