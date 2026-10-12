package com.exemplo.website.infrastructure.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(
        value = "recuperar_senha_codigo",
        timeToLive = 180
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecuperarSenhaCodigoRedisEntity {

    @Id
    private Long usuarioId;

    private Integer codigo;
}