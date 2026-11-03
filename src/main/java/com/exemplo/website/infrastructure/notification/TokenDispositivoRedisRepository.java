package com.exemplo.website.infrastructure.notification;

import com.exemplo.website.domain.repository.TokenDispositivoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

/** Lê tokens do keyspace compartilhado criado pela API mobile. */
@Repository
public class TokenDispositivoRedisRepository implements TokenDispositivoRepository {
    private final String restUrl;
    private final String restToken;
    private volatile RestClient restClient;

    public TokenDispositivoRedisRepository(
            @Value("${app.redis.upstash.rest-url:}") String restUrl,
            @Value("${app.redis.upstash.rest-token:}") String restToken
    ) {
        this.restUrl = restUrl;
        this.restToken = restToken;
    }

    @Override
    public List<String> listarPorUsuario(Long usuarioId) {
        JsonNode ids = executar("SMEMBERS", "token_dispositivo:usuarioId:" + usuarioId);
        if (ids == null || !ids.isArray()) return List.of();
        List<String> tokens = new ArrayList<>();
        for (JsonNode id : ids) {
            // Spring Data Redis usa o valor de @Id como membro do índice secundário.
            if (!id.asString().isBlank()) tokens.add(id.asString());
        }
        return tokens;
    }

    private JsonNode executar(String... comando) {
        if (restUrl.isBlank() || restToken.isBlank()) {
            throw new IllegalStateException("Configure UPSTASH_REDIS_REST_URL e UPSTASH_REDIS_REST_TOKEN");
        }
        try {
            JsonNode resposta = restClient().post().uri(restUrl).contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + restToken).body(List.of(comando))
                    .retrieve().body(JsonNode.class);
            if (resposta == null || resposta.has("error")) {
                throw new IllegalStateException("O Upstash Redis REST rejeitou o comando");
            }
            return resposta.get("result");
        } catch (RestClientException exception) {
            throw new IllegalStateException("Falha na comunicação com Redis", exception);
        }
    }

    private RestClient restClient() {
        RestClient atual = restClient;
        if (atual == null) {
            synchronized (this) {
                atual = restClient;
                if (atual == null) restClient = atual = RestClient.create();
            }
        }
        return atual;
    }
}
