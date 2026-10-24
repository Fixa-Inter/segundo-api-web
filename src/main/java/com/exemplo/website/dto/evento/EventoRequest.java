package com.exemplo.website.dto.evento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record EventoRequest(

        @NotNull(message = "O usuário é obrigatório")
        Long usuarioId,

        @NotNull(message = "O local é obrigatório")
        Long localEnderecoId,

        @NotBlank(message = "O título é obrigatório")
        @Size(max = 100, message = "O título deve possuir no máximo 100 caracteres")
        String titulo,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 255, message = "A descrição deve possuir no máximo 255 caracteres")
        String descricao,

        @NotBlank(message = "A descrição do local é obrigatória")
        @Size(max = 255, message = "A descrição do local deve possuir no máximo 255 caracteres")
        String descricaoLocal,

        @Size(max = 255, message = "A observação deve possuir no máximo 255 caracteres")
        String observacao,

        @NotNull(message = "A data de início é obrigatória")
        LocalDateTime dataHoraInicio,

        @NotNull(message = "A data de fim é obrigatória")
        LocalDateTime dataHoraFim
) {
}
