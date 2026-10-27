package com.exemplo.website.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final MacAlgorithm jwtMacAlgorithm;
    private final long expirationSeconds;
    private final SecureRandom secureRandom = new SecureRandom();

    public JwtService(
            JwtEncoder jwtEncoder,
            MacAlgorithm jwtMacAlgorithm,
            @Value("${security.jwt.expiration-seconds}")
            long expirationSeconds
    ) {
        this.jwtEncoder = jwtEncoder;
        this.jwtMacAlgorithm = jwtMacAlgorithm;
        this.expirationSeconds = expirationSeconds;
    }

    public String gerarToken(UsuarioPrincipal principal) {

        Instant agora = Instant.now();

        String tipoAcesso =
                principal.getUsuario()
                        .getTipoAcesso()
                        .name();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("segundo-api-web")
                .issuedAt(agora)
                .expiresAt(
                        agora.plusSeconds(expirationSeconds)
                )
                .subject(principal.getUsuario().getEmail())
                .claim("userId", principal.getUsuario().getId())
                .claim("role", tipoAcesso)
                .build();

        JwsHeader header =
                JwsHeader.with(jwtMacAlgorithm)
                        .build();

        return jwtEncoder
                .encode(
                        JwtEncoderParameters.from(
                                header,
                                claims
                        )
                )
                .getTokenValue();
    }

    public String gerarRefreshToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
