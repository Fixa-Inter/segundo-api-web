package com.exemplo.website.repository;

import java.time.Duration;
import java.util.Optional;

public interface RefreshTokenRepository {

    void salvar(String token, Long usuarioId, Duration validade);

    Optional<Long> consumir(String token);

    void remover(String token);

    void removerTodosPorUsuarioId(Long usuarioId);
}
