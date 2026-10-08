package com.exemplo.website.dto.usuario;

import com.exemplo.website.model.Enum.TipoAcesso;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        Long enderecoId,
        String nomeCompleto,
        String email,
        TipoAcesso tipoAcesso,
        String cargo,
        LocalDate dataNascimento,
        LocalDateTime dataCriacao,
        Boolean estaAtivo,
        Boolean primeiroAcesso
) {
}