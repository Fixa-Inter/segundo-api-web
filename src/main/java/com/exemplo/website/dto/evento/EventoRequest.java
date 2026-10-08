package com.exemplo.website.dto.evento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EventoRequest(

        Long usuarioId,

        @NotNull(message = "O local é obrigatório")
        Long localEnderecoId,

        @NotBlank(message = "O título é obrigatório")
        String titulo,

        @NotBlank(message = "A descrição é obrigatória")
        String descricao,

        @NotBlank(message = "A descrição do local é obrigatória")
        String descricaoLocal,

        String observacao,

        @NotNull(message = "A data de início é obrigatória")
        LocalDateTime dataHoraInicio,

        @NotNull(message = "A data de fim é obrigatória")
        LocalDateTime dataHoraFim
) {
}