package com.exemplo.website.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final long expirationSeconds;

    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${security.jwt.expiration-seconds}")
            long expirationSeconds
    ) {
        this.jwtEncoder = jwtEncoder;
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
                .subject(
                        principal.getUsuario()
                                .getId()
                                .toString()
                )
                .claim(
                        "roles",
                        List.of(tipoAcesso)
                )
                .build();

        JwsHeader header =
                JwsHeader.with(MacAlgorithm.HS256)
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
}