package com.exemplo.website.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigTest {

    @Test
    void passwordEncoderUsesBcrypt2aWithTwelveRounds() {
        PasswordEncoder encoder = new SecurityConfig().passwordEncoder();
        String rawPassword = "SenhaTeste123";
        String encodedPassword = encoder.encode(rawPassword);

        assertTrue(encodedPassword.startsWith("$2a$12$"));
        assertTrue(encoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void passwordEncoderStillVerifiesExisting2yHashes() {
        String rawPassword = "SenhaTeste123";
        String existing2yHash = new BCryptPasswordEncoder(
                BCryptPasswordEncoder.BCryptVersion.$2Y,
                12
        ).encode(rawPassword);

        assertTrue(new SecurityConfig().passwordEncoder().matches(rawPassword, existing2yHash));
    }

    @Test
    void deveConverterRolesDoJwtEmAuthorities() {
        SecurityConfig securityConfig = new SecurityConfig();

        Instant agora = Instant.now();
        Jwt jwt = new Jwt(
                "token",
                agora,
                agora.plusSeconds(60),
                Map.of("alg", "HS256"),
                Map.of(
                        "sub", "42",
                        "roles", List.of("GESTOR")
                )
        );

        var authentication = securityConfig
                .jwtAuthenticationConverter()
                .convert(jwt);

        assertNotNull(authentication);
        assertEquals("42", authentication.getName());
        assertTrue(
                authentication.getAuthorities()
                        .stream()
                        .map(Object::toString)
                        .anyMatch("ROLE_GESTOR"::equals)
        );
    }
}
