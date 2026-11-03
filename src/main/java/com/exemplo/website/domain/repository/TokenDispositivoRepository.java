package com.exemplo.website.domain.repository;

import java.util.List;

public interface TokenDispositivoRepository {
    List<String> listarPorUsuario(Long usuarioId);
}
