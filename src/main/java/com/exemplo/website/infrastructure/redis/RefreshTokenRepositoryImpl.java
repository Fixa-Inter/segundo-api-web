package com.exemplo.website.infrastructure.redis;

import com.exemplo.website.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.JsonNode;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

@Repository
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private static final String PREFIXO_CHAVE = "refresh_token:";

    private volatile RestClient restClient;
    private final String restUrl;
    private final String restToken;

    public RefreshTokenRepositoryImpl(
            @Value("${app.redis.upstash.rest-url:}") String restUrl,
            @Value("${app.redis.upstash.rest-token:}") String restToken
    ) {
        this.restUrl = restUrl;
        this.restToken = restToken;
    }

    @Override
    public void salvar(String token, Long usuarioId, Duration validade) {
        String hashToken = hash(token);
        executarComando(
                "SET",
                chaveHash(hashToken),
                usuarioId.toString(),
                "EX",
                Long.toString(validade.toSeconds())
        );
        executarComando("SADD", indiceUsuario(usuarioId), hashToken);
        executarComando("EXPIRE", indiceUsuario(usuarioId), Long.toString(validade.toSeconds()));
    }

    @Override
    public Optional<Long> consumir(String token) {
        String hashToken = hash(token);
        JsonNode resultado = executarComando("GETDEL", chaveHash(hashToken));
        if (resultado == null || resultado.isNull()) {
            return Optional.empty();
        }

        try {
            Long usuarioId = Long.parseLong(resultado.asString());
            executarComando("SREM", indiceUsuario(usuarioId), hashToken);
            return Optional.of(usuarioId);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Refresh token armazenado inválido", exception);
        }
    }

    @Override
    public void remover(String token) {
        String hashToken = hash(token);
        JsonNode resultado = executarComando("GETDEL", chaveHash(hashToken));
        if (resultado != null && !resultado.isNull()) {
            try {
                Long usuarioId = Long.parseLong(resultado.asString());
                executarComando("SREM", indiceUsuario(usuarioId), hashToken);
            } catch (NumberFormatException exception) {
                throw new IllegalStateException("Refresh token armazenado inválido", exception);
            }
        }
    }

    @Override
    public void removerTodosPorUsuarioId(Long usuarioId) {
        JsonNode hashes = executarComando("SMEMBERS", indiceUsuario(usuarioId));
        if (hashes != null && hashes.isArray() && !hashes.isEmpty()) {
            String[] comando = new String[hashes.size() + 1];
            comando[0] = "DEL";
            for (int index = 0; index < hashes.size(); index++) {
                comando[index + 1] = chaveHash(hashes.get(index).asString());
            }
            executarComando(comando);
        }
        executarComando("DEL", indiceUsuario(usuarioId));
    }

    private JsonNode executarComando(String... comando) {
        if (restUrl.isBlank() || restToken.isBlank()) {
            throw new IllegalStateException(
                    "Configure UPSTASH_REDIS_REST_URL e UPSTASH_REDIS_REST_TOKEN"
            );
        }

        try {
            JsonNode resposta = restClient().post()
                    .uri(restUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + restToken)
                    .body(List.of(comando))
                    .retrieve()
                    .body(JsonNode.class);

            if (resposta == null || resposta.has("error")) {
                throw new IllegalStateException("O Upstash Redis REST rejeitou o comando");
            }
            return resposta.get("result");
        } catch (RestClientException exception) {
            throw new IllegalStateException(
                    "Falha na comunicação com o Upstash Redis REST",
                    exception
            );
        }
    }

    private String chaveHash(String hashToken) {
        return PREFIXO_CHAVE + hashToken;
    }

    private String indiceUsuario(Long usuarioId) {
        return "refresh_token_user:" + usuarioId;
    }

    private String hash(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 não disponível", exception);
        }
    }

    private RestClient restClient() {
        RestClient atual = restClient;
        if (atual == null) {
            synchronized (this) {
                atual = restClient;
                if (atual == null) {
                    atual = RestClient.create();
                    restClient = atual;
                }
            }
        }
        return atual;
    }
}
