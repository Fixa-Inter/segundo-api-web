package com.exemplo.website.dto.perfil;

import java.time.LocalDate;

public record PerfilUpdateRequest(
        String nomeCompleto,
        String email,
        LocalDate dataNascimento
) {
}