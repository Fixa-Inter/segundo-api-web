package com.exemplo.website.dto.evento;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record EventoUpdateRequest(
        Long localEnderecoId,
        @Size(max = 100, message = "O título deve possuir no máximo 100 caracteres")
        @Pattern(regexp = ".*\\S.*", message = "O título não pode ficar em branco")
        String titulo,
        @Size(max = 255, message = "A descrição deve possuir no máximo 255 caracteres")
        @Pattern(regexp = ".*\\S.*", message = "A descrição não pode ficar em branco")
        String descricao,
        @Size(max = 255, message = "A descrição do local deve possuir no máximo 255 caracteres")
        @Pattern(regexp = ".*\\S.*", message = "A descrição do local não pode ficar em branco")
        String descricaoLocal,
        @Size(max = 255, message = "A observação deve possuir no máximo 255 caracteres")
        String observacao,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim
) {
}
