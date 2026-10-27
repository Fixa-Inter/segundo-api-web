package com.exemplo.website.security;

import com.exemplo.website.config.JwtConfig;
import com.exemplo.website.model.Enum.TipoAcesso;
import com.exemplo.website.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;

import javax.crypto.SecretKey;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    private final JwtConfig config = new JwtConfig();

    @Test
    void selectsAlgorithmByUtf8ByteLengthAndPreservesClaims() {
        assertToken("á".repeat(16), "HS256"); // 32 UTF-8 bytes
        assertToken("á".repeat(24), "HS384"); // 48 UTF-8 bytes
        assertToken("á".repeat(32), "HS512"); // 64 UTF-8 bytes
    }

    @Test
    void rejectsShortSecret() {
        assertThrows(IllegalArgumentException.class,
                () -> config.jwtSecretKey("á".repeat(15)));
    }

    @Test
    void rejectsExpiredTokenAndDifferentSigningKey() {
        SecretKey key = config.jwtSecretKey("a".repeat(32));
        var algorithm = config.jwtMacAlgorithm(key);
        JwtDecoder decoder = config.jwtDecoder(key, algorithm);

        Instant now = Instant.now();
        String expired = config.jwtEncoder(key, algorithm).encode(JwtEncoderParameters.from(
                JwsHeader.with(algorithm).build(),
                JwtClaimsSet.builder()
                        .subject("user@example.com")
                        .issuedAt(now.minusSeconds(120))
                        .expiresAt(now.minusSeconds(60))
                        .build()
        )).getTokenValue();
        assertThrows(JwtException.class, () -> decoder.decode(expired));

        String valid = new JwtService(config.jwtEncoder(key, algorithm), algorithm, 3600)
                .gerarToken(principal());
        SecretKey otherKey = config.jwtSecretKey("b".repeat(32));
        assertThrows(JwtException.class,
                () -> config.jwtDecoder(otherKey, config.jwtMacAlgorithm(otherKey)).decode(valid));
    }

    @Test
    void rejectsTokenWithoutExpiration() {
        SecretKey key = config.jwtSecretKey("a".repeat(32));
        var algorithm = config.jwtMacAlgorithm(key);
        String token = config.jwtEncoder(key, algorithm).encode(JwtEncoderParameters.from(
                JwsHeader.with(algorithm).build(),
                JwtClaimsSet.builder().subject("user@example.com").build()
        )).getTokenValue();

        assertThrows(JwtException.class, () -> config.jwtDecoder(key, algorithm).decode(token));
    }

    private void assertToken(String secret, String expectedAlgorithm) {
        SecretKey key = config.jwtSecretKey(secret);
        var algorithm = config.jwtMacAlgorithm(key);
        JwtService service = new JwtService(config.jwtEncoder(key, algorithm), algorithm, 3600);

        Jwt jwt = config.jwtDecoder(key, algorithm).decode(service.gerarToken(principal()));

        assertEquals(expectedAlgorithm, jwt.getHeaders().get("alg"));
        assertEquals("user@example.com", jwt.getSubject());
        assertEquals(42L, ((Number) jwt.getClaim("userId")).longValue());
        assertEquals("42", jwt.getClaimAsString("userId"));
        assertEquals("GESTOR", jwt.getClaimAsString("role"));
    }

    private UsuarioPrincipal principal() {
        Usuario usuario = new Usuario();
        usuario.setId(42L);
        usuario.setEmail("user@example.com");
        usuario.setTipoAcesso(TipoAcesso.GESTOR);
        return new UsuarioPrincipal(usuario);
    }
}
