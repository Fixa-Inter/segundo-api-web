package com.exemplo.website.security;

import com.exemplo.website.model.Enum.TipoAcesso;
import com.exemplo.website.model.Unidade;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccessPolicyServiceTest {

    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final AccessPolicyService accessPolicyService = new AccessPolicyService(usuarioRepository);

    @Test
    void gestorCanAccessOnlyOwnUnit() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario(
                10L, TipoAcesso.GESTOR, 1L, true
        )));

        accessPolicyService.exigirAcessoUnidade(10L, 1L);
        assertThrows(AccessDeniedException.class,
                () -> accessPolicyService.exigirAcessoUnidade(10L, 2L));
    }

    @Test
    void administratorCanAccessAnyUnit() {
        when(usuarioRepository.findById(20L)).thenReturn(Optional.of(usuario(
                20L, TipoAcesso.ADMINISTRADOR, 1L, true
        )));

        accessPolicyService.exigirAcessoUnidade(20L, 999L);
    }

    @Test
    void inactiveActorIsDenied() {
        when(usuarioRepository.findById(30L)).thenReturn(Optional.of(usuario(
                30L, TipoAcesso.GESTOR, 1L, false
        )));

        assertThrows(AccessDeniedException.class,
                () -> accessPolicyService.exigirAcessoUnidade(30L, 1L));
    }

    private Usuario usuario(Long id, TipoAcesso tipo, Long unidadeId, boolean ativo) {
        Unidade unidade = new Unidade();
        unidade.setId(unidadeId);

        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setTipoAcesso(tipo);
        usuario.setUnidade(unidade);
        usuario.setEstaAtivo(ativo);
        return usuario;
    }
}
