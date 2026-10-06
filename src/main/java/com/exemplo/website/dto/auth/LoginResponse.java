package com.exemplo.website.dto.auth;

public record LoginResponse(
        Long usuarioId,
        String nome,
        String tipoAcesso,
        String token
) {
}