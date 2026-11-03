package com.exemplo.website.infrastructure.notification;

import com.exemplo.website.domain.model.Notificacao;
import com.exemplo.website.domain.repository.NotificacaoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.JsonNode;

import java.util.List;

/** Persiste a notificação no mesmo keyspace e formato Redis consumidos pela API mobile. */
@Repository
public class NotificacaoRedisRepository implements NotificacaoRepository {
    private static final long TTL_SEGUNDOS = 2_592_000L;
    private volatile RestClient restClient;
    private final String restUrl;
    private final String restToken;

    public NotificacaoRedisRepository(
            @Value("${app.redis.upstash.rest-url:}") String restUrl,
            @Value("${app.redis.upstash.rest-token:}") String restToken
    ) {
        this.restUrl = restUrl;
        this.restToken = restToken;
    }

    @Override
    public void salvar(Notificacao notificacao) {
        String chave = "notificacao:" + notificacao.id();
        executar("HSET", chave,
                "id", notificacao.id(),
                "usuarioId", notificacao.usuarioId().toString(),
                "tipoNotificacao", Integer.toString(notificacao.tipoNotificacao()),
                "referenciaId", notificacao.referenciaId(),
                "mensagem", notificacao.mensagem(),
                "dataCriacao", notificacao.dataCriacao().toString(),
                "visualizada", Boolean.toString(notificacao.visualizada()));
        executar("EXPIRE", chave, Long.toString(TTL_SEGUNDOS));

        String indiceUsuario = "notificacao:usuarioId:" + notificacao.usuarioId();
        executar("SADD", indiceUsuario, notificacao.id());
        executar("EXPIRE", indiceUsuario, Long.toString(TTL_SEGUNDOS));
    }

    private JsonNode executar(String... comando) {
        if (restUrl.isBlank() || restToken.isBlank()) {
            throw new IllegalStateException("Configure UPSTASH_REDIS_REST_URL e UPSTASH_REDIS_REST_TOKEN");
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
            throw new IllegalStateException("Falha na comunicação com o Upstash Redis REST", exception);
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
