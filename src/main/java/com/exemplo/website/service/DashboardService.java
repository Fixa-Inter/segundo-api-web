package com.exemplo.website.service;

import com.exemplo.website.dto.dashboard.DashboardEmbedResponse;
import com.exemplo.website.infrastructure.databricks.DatabricksEmbedClient;
import com.exemplo.website.infrastructure.databricks.DatabricksEmbedClient.TokenEscopado;
import com.exemplo.website.model.Unidade;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.security.AccessPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AccessPolicyService accessPolicyService;
    private final DatabricksEmbedClient databricksEmbedClient;

    public DashboardEmbedResponse gerarEmbed(Long usuarioAutenticadoId) {

        Usuario usuario = accessPolicyService.usuarioAutenticado(usuarioAutenticadoId);

        Unidade unidade = usuario.getUnidade();
        if (unidade == null || !Boolean.TRUE.equals(unidade.getEstaAtivo())) {
            throw new AccessDeniedException("Unidade do usuário inativa ou não encontrada");
        }

        String externalViewerId = "usuario-" + usuario.getId();
        String externalValue = String.valueOf(unidade.getId());

        TokenEscopado token = databricksEmbedClient.gerarTokenEscopado(
                externalViewerId,
                externalValue
        );

        return new DashboardEmbedResponse(
                databricksEmbedClient.getInstanceUrl(),
                databricksEmbedClient.getWorkspaceId(),
                databricksEmbedClient.getDashboardId(),
                token.accessToken(),
                token.expiresIn()
        );
    }
}
