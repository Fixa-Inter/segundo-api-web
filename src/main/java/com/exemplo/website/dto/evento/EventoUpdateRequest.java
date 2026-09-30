package com.exemplo.website.dto.evento;

import java.time.LocalDateTime;

public record EventoUpdateRequest(
        Long localEnderecoId,
        String titulo,
        String descricao,
        String descricaoLocal,
        String observacao,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim
) {
}