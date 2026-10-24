package com.exemplo.website.infrastructure.redis;

import com.exemplo.website.model.RecuperarSenhaCodigo;
import com.exemplo.website.repository.RecuperarSenhaCodigoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Optional;

@Repository
public class RecuperarSenhaCodigoRepositoryImpl
        implements RecuperarSenhaCodigoRepository {

    private static final long TEMPO_EXPIRACAO_SEGUNDOS = 180;
    private static final String PREFIXO_CHAVE = "recuperar_senha_codigo:";

    private volatile RestClient restClient;
    private final String restUrl;
    private final String restToken;

    public RecuperarSenhaCodigoRepositoryImpl(
            @Value("${app.redis.upstash.rest-url:}") String restUrl,
            @Value("${app.redis.upstash.rest-token:}") String restToken
    ) {
        this.restUrl = restUrl;
        this.restToken = restToken;
    }

    @Override
    public void salvar(RecuperarSenhaCodigo recuperarSenhaCodigo) {

        executarComando(
                "SET",
                chave(recuperarSenhaCodigo.getUsuarioId()),
                recuperarSenhaCodigo.getCodigo().toString(),
                "EX",
                String.valueOf(TEMPO_EXPIRACAO_SEGUNDOS)
        );
    }

    @Override
    public Optional<RecuperarSenhaCodigo> buscarPorUsuarioId(Long usuarioId) {

        JsonNode resultado = executarComando("GET", chave(usuarioId));

        if (resultado == null || resultado.isNull()) {
            return Optional.empty();
        }

        return Optional.of(new RecuperarSenhaCodigo(
                usuarioId,
                resultado.asInt()
        ));
    }

    @Override
    public void removerPorUsuarioId(Long usuarioId) {
        executarComando("DEL", chave(usuarioId));
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

            if (resposta == null) {
                throw new IllegalStateException(
                        "O Upstash Redis REST retornou uma resposta vazia"
                );
            }

            if (resposta.has("error")) {
                throw new IllegalStateException(
                        "O Upstash Redis REST rejeitou o comando: "
                                + resposta.get("error").asText()
                );
            }

            return resposta.get("result");
        } catch (RestClientException exception) {
            throw new IllegalStateException(
                    "Falha na comunicação com o Upstash Redis REST",
                    exception
            );
        }
    }

    private String chave(Long usuarioId) {
        return PREFIXO_CHAVE + usuarioId;
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
