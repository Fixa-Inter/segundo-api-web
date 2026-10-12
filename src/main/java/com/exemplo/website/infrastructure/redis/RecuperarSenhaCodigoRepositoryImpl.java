package com.exemplo.website.infrastructure.redis;

import com.exemplo.website.infrastructure.redis.entity.RecuperarSenhaCodigoRedisEntity;
import com.exemplo.website.infrastructure.redis.repository.RecuperarSenhaCodigoRedisRepository;
import com.exemplo.website.model.RecuperarSenhaCodigo;
import com.exemplo.website.repository.RecuperarSenhaCodigoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RecuperarSenhaCodigoRepositoryImpl
        implements RecuperarSenhaCodigoRepository {

    private final RecuperarSenhaCodigoRedisRepository repository;

    @Override
    public void salvar(RecuperarSenhaCodigo recuperarSenhaCodigo) {

        RecuperarSenhaCodigoRedisEntity entity =
                new RecuperarSenhaCodigoRedisEntity(
                        recuperarSenhaCodigo.getUsuarioId(),
                        recuperarSenhaCodigo.getCodigo()
                );

        repository.save(entity);
    }

    @Override
    public Optional<RecuperarSenhaCodigo> buscarPorUsuarioId(Long usuarioId) {

        return repository
                .findById(usuarioId)
                .map(entity -> new RecuperarSenhaCodigo(
                        entity.getUsuarioId(),
                        entity.getCodigo()
                ));
    }

    @Override
    public void removerPorUsuarioId(Long usuarioId) {
        repository.deleteById(usuarioId);
    }
}