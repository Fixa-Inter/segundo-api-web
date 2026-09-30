package com.exemplo.website.dto.usuario;

import com.exemplo.website.model.Enum.TipoAcesso;

import java.time.LocalDate;

public record UsuarioRequest(
        Long enderecoId,
        String nomeCompleto,
        String email,
        TipoAcesso tipoAcesso,
        String senha,
        LocalDate dataNascimento
) {
}