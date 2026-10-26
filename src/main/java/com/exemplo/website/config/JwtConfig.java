package com.exemplo.website.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Configuration
public class JwtConfig {

    @Bean
    public SecretKey jwtSecretKey(
            @Value("${security.jwt.secret}") String secret
    ) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                    "JWT_SECRET deve possuir pelo menos 256 bits"
            );
        }

        String algorithm = keyBytes.length >= 64 ? "HmacSHA512"
                : keyBytes.length >= 48 ? "HmacSHA384" : "HmacSHA256";
        return new SecretKeySpec(keyBytes, algorithm);
    }

    @Bean
    public MacAlgorithm jwtMacAlgorithm(SecretKey secretKey) {
        int length = secretKey.getEncoded().length;
        return length >= 64 ? MacAlgorithm.HS512
                : length >= 48 ? MacAlgorithm.HS384 : MacAlgorithm.HS256;
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey secretKey, MacAlgorithm jwtMacAlgorithm) {
        return NimbusJwtEncoder
                .withSecretKey(secretKey)
                .algorithm(jwtMacAlgorithm)
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey secretKey, MacAlgorithm jwtMacAlgorithm) {

        NimbusJwtDecoder decoder =
                NimbusJwtDecoder
                        .withSecretKey(secretKey)
                        .macAlgorithm(jwtMacAlgorithm)
                        .build();

        decoder.setJwtValidator(jwt -> {
            if (jwt.getExpiresAt() == null) {
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token", "JWT sem expiração", null)
                );
            }
            return new JwtTimestampValidator(Duration.ZERO).validate(jwt);
        });

        return decoder;
    }
}
