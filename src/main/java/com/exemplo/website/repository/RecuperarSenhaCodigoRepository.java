package com.exemplo.website.repository;

import com.exemplo.website.model.RecuperarSenhaCodigo;

import java.util.Optional;

public interface RecuperarSenhaCodigoRepository {

    void salvar(RecuperarSenhaCodigo recuperarSenhaCodigo);

    Optional<RecuperarSenhaCodigo> buscarPorUsuarioId(Long usuarioId);

    void removerPorUsuarioId(Long usuarioId);
}