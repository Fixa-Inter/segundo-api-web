package com.exemplo.website.dto.perfil;

import com.exemplo.website.dto.foto.FotoResponse;
import com.exemplo.website.model.Enum.TipoAcesso;

import java.time.LocalDate;

public record PerfilResponse(
        Long id,
        String nomeCompleto,
        String email,
        TipoAcesso tipoAcesso,
        String cargo,
        LocalDate dataNascimento,
        FotoResponse foto
) {
}