package com.exemplo.website.dto.foto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FotoRequest(
        @NotBlank(message = "A URL da foto é obrigatória")
        @Size(max = 255, message = "A URL da foto deve ter no máximo 255 caracteres")
        String url
) {
}
