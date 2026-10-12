package com.exemplo.website.infrastructure.redis.repository;

import com.exemplo.website.infrastructure.redis.entity.RecuperarSenhaCodigoRedisEntity;
import org.springframework.data.repository.CrudRepository;

public interface RecuperarSenhaCodigoRedisRepository
        extends CrudRepository<RecuperarSenhaCodigoRedisEntity, Long> {
}