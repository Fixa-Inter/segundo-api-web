package com.exemplo.website.dto.aptidao;

import java.time.LocalDateTime;

public record AptidaoResponse(
        Long id,
        Long usuarioId,
        String categoriaProblema,
        Double nota,
        LocalDateTime dataCriacao,
        Boolean estaAtivo
) {
}