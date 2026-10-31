package com.exemplo.website.service;

import com.exemplo.website.dto.dashboard.DashboardEmbedResponse;
import com.exemplo.website.infrastructure.databricks.DatabricksEmbedClient;
import com.exemplo.website.infrastructure.databricks.DatabricksEmbedClient.TokenEscopado;
import com.exemplo.website.model.Unidade;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.security.AccessPolicyService;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DashboardServiceTest {

    private final AccessPolicyService accessPolicyService = mock(AccessPolicyService.class);
    private final DatabricksEmbedClient databricksEmbedClient = mock(DatabricksEmbedClient.class);
    private final DashboardService service = new DashboardService(
            accessPolicyService,
            databricksEmbedClient
    );

    @Test
    void geraTokenRestritoAUnidadeDoUsuarioAutenticado() {
        Unidade unidade = new Unidade();
        unidade.setId(72L);
        unidade.setEstaAtivo(true);

        Usuario usuario = new Usuario();
        usuario.setId(147L);
        usuario.setUnidade(unidade);

        when(accessPolicyService.usuarioAutenticado(147L)).thenReturn(usuario);
        when(databricksEmbedClient.getInstanceUrl()).thenReturn("https://workspace.databricks.com");
        when(databricksEmbedClient.getWorkspaceId()).thenReturn("123456789");
        when(databricksEmbedClient.getDashboardId()).thenReturn("dashboard-abc");
        when(databricksEmbedClient.gerarTokenEscopado("usuario-147", "72"))
                .thenReturn(new TokenEscopado("token-restrito", 3600L));

        DashboardEmbedResponse response = service.gerarEmbed(147L);

        assertEquals("https://workspace.databricks.com", response.instanceUrl());
        assertEquals("123456789", response.workspaceId());
        assertEquals("dashboard-abc", response.dashboardId());
        assertEquals("token-restrito", response.token());
        assertEquals(3600L, response.expiresIn());
        verify(databricksEmbedClient).gerarTokenEscopado("usuario-147", "72");
    }

    @Test
    void bloqueiaEmbedQuandoUnidadeDoUsuarioEstaInativa() {
        Unidade unidade = new Unidade();
        unidade.setId(72L);
        unidade.setEstaAtivo(false);

        Usuario usuario = new Usuario();
        usuario.setId(147L);
        usuario.setUnidade(unidade);

        when(accessPolicyService.usuarioAutenticado(147L)).thenReturn(usuario);

        assertThrows(AccessDeniedException.class, () -> service.gerarEmbed(147L));
        verify(databricksEmbedClient, never()).gerarTokenEscopado("usuario-147", "72");
    }

    @Test
    void bloqueiaEmbedQuandoUsuarioNaoTemUnidade() {
        Usuario usuario = new Usuario();
        usuario.setId(147L);

        when(accessPolicyService.usuarioAutenticado(147L)).thenReturn(usuario);

        assertThrows(AccessDeniedException.class, () -> service.gerarEmbed(147L));
        verify(databricksEmbedClient, never()).gerarTokenEscopado("usuario-147", "72");
    }
}
