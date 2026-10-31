package com.exemplo.website.infrastructure.databricks;

import com.exemplo.website.exception.DatabricksIntegrationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.util.Map;

@Component
public class DatabricksEmbedClient {

    private static final String AUTHORIZATION_DETAILS = "authorization_details";

    private final RestClient restClient;
    private final String instanceUrl;
    private final String workspaceId;
    private final String dashboardId;
    private final String clientId;
    private final String clientSecret;

    public DatabricksEmbedClient(
            @Value("${app.databricks.instance-url:}") String instanceUrl,
            @Value("${app.databricks.workspace-id:}") String workspaceId,
            @Value("${app.databricks.dashboard-id:}") String dashboardId,
            @Value("${app.databricks.client-id:}") String clientId,
            @Value("${app.databricks.client-secret:}") String clientSecret
    ) {
        this.instanceUrl = instanceUrl.endsWith("/")
                ? instanceUrl.substring(0, instanceUrl.length() - 1)
                : instanceUrl;
        this.workspaceId = workspaceId;
        this.dashboardId = dashboardId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(10));
        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
    }

    public String getInstanceUrl() {
        return instanceUrl;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public String getDashboardId() {
        return dashboardId;
    }

    /**
     * Gera um token de embed (validade de 1h) restrito ao contexto informado.
     *
     * @param externalViewerId
     * @param externalValue
     */

    public TokenEscopado gerarTokenEscopado(String externalViewerId, String externalValue) {
        validarConfiguracao();

        try {

            JsonNode tokenAmplo = restClient.post()
                    .uri(instanceUrl + "/oidc/v1/token?o={workspaceId}", workspaceId)
                    .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formulario(Map.of(
                            "grant_type", "client_credentials",
                            "scope", "all-apis"
                    )))
                    .retrieve()
                    .body(JsonNode.class);

            String tokenAmploValor = textoObrigatorio(tokenAmplo, "access_token");

            JsonNode tokenInfo = restClient.get()
                    .uri(instanceUrl + "/api/2.0/lakeview/dashboards/{dashboardId}/published/tokeninfo"
                                    + "?o={workspaceId}&external_viewer_id={viewer}&external_value={value}",
                            dashboardId, workspaceId, externalViewerId, externalValue)
                    .headers(headers -> headers.setBearerAuth(tokenAmploValor))
                    .retrieve()
                    .body(JsonNode.class);

            if (tokenInfo == null || tokenInfo.isNull()) {
                throw new DatabricksIntegrationException("Resposta vazia do tokeninfo do Databricks");
            }

            // 3. Token restrito (é o único que sai do backend)
            MultiValueMap<String, String> formularioEscopado = new LinkedMultiValueMap<>();
            formularioEscopado.add("grant_type", "client_credentials");
            tokenInfo.properties().forEach(entry -> {
                if (!AUTHORIZATION_DETAILS.equals(entry.getKey())) {
                    formularioEscopado.add(entry.getKey(), entry.getValue().asText());
                }
            });
            JsonNode authorizationDetails = tokenInfo.get(AUTHORIZATION_DETAILS);
            if (authorizationDetails != null && !authorizationDetails.isNull()) {
                formularioEscopado.add(AUTHORIZATION_DETAILS, authorizationDetails.toString());
            }

            JsonNode tokenRestrito = restClient.post()
                    .uri(instanceUrl + "/oidc/v1/token?o={workspaceId}", workspaceId)
                    .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formularioEscopado)
                    .retrieve()
                    .body(JsonNode.class);

            return new TokenEscopado(
                    textoObrigatorio(tokenRestrito, "access_token"),
                    tokenRestrito.path("expires_in").asLong(3600)
            );
        } catch (RestClientException exception) {
            throw new DatabricksIntegrationException(
                    "Falha na comunicação com o Databricks", exception
            );
        }
    }

    private MultiValueMap<String, String> formulario(Map<String, String> campos) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        campos.forEach(form::add);
        return form;
    }

    private String textoObrigatorio(JsonNode node, String campo) {
        if (node == null || !node.hasNonNull(campo)) {
            throw new DatabricksIntegrationException(
                    "Resposta do Databricks sem o campo '" + campo + "'"
            );
        }
        return node.get(campo).asText();
    }

    private void validarConfiguracao() {
        if (instanceUrl.isBlank() || workspaceId.isBlank() || dashboardId.isBlank()
                || clientId.isBlank() || clientSecret.isBlank()) {
            throw new DatabricksIntegrationException(
                    "Configure DATABRICKS_INSTANCE_URL, DATABRICKS_WORKSPACE_ID, "
                            + "DATABRICKS_DASHBOARD_ID, DATABRICKS_CLIENT_ID e DATABRICKS_CLIENT_SECRET"
            );
        }
    }

    public record TokenEscopado(String accessToken, Long expiresIn) {
    }
}