package com.exemplo.website.service;

import com.exemplo.website.dto.usuario.UsuarioRequest;
import com.exemplo.website.dto.usuario.UsuarioResponse;
import com.exemplo.website.model.Enum.TipoAcesso;
import com.exemplo.website.model.Unidade;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.model.Instituicao;
import com.exemplo.website.repository.UnidadeRepository;
import com.exemplo.website.repository.UsuarioRepository;
import com.exemplo.website.security.AccessPolicyService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    private final UnidadeRepository unidadeRepository = mock(UnidadeRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final AccessPolicyService accessPolicyService = mock(AccessPolicyService.class);
    private final UsuarioService service = new UsuarioService(
            unidadeRepository, passwordEncoder, usuarioRepository, accessPolicyService);

    @BeforeEach
    void setUp() {
        Unidade unidade = new Unidade();
        unidade.setId(5L);
        Instituicao instituicao = new Instituicao();
        instituicao.setDominioEmail("teste.com");
        unidade.setInstituicao(instituicao);
        when(unidadeRepository.findById(5L)).thenReturn(Optional.of(unidade));
        when(usuarioRepository.existsByEmail("tecnico@teste.com")).thenReturn(false);
        when(passwordEncoder.encode("Senha123")).thenReturn("hash-bcrypt");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(15L);
            return usuario;
        });
    }

    @Test
    void cadastraTecnicoNormalizandoDadosEArmazenandoHash() {
        when(accessPolicyService.ehAdministrador(2L)).thenReturn(false);
        UsuarioRequest request = new UsuarioRequest(
                5L, "  Técnico de Teste  ", "  TECNICO@TESTE.COM ",
                TipoAcesso.TECNICO, "  Técnico  ", "Senha123", LocalDate.of(2000, 1, 1));

        UsuarioResponse response = service.cadastrar(request, 2L);

        assertEquals(15L, response.id());
        assertEquals("tecnico@teste.com", response.email());
        assertEquals("Técnico de Teste", response.nomeCompleto());
        verify(accessPolicyService).exigirAcessoUnidade(2L, 5L);
        verify(usuarioRepository).save(argThat(usuario ->
                "hash-bcrypt".equals(usuario.getSenhaHash())
                        && "Técnico".equals(usuario.getCargo())
                        && usuario.getTipoAcesso() == TipoAcesso.TECNICO));
    }

    @Test
    void impedeGestorDeCadastrarOutroGestor() {
        when(accessPolicyService.ehAdministrador(2L)).thenReturn(false);
        UsuarioRequest request = new UsuarioRequest(
                5L, "Gestor", "gestor@teste.com", TipoAcesso.GESTOR,
                "Gestor", "Senha123", LocalDate.of(2000, 1, 1));

        assertThrows(AccessDeniedException.class, () -> service.cadastrar(request, 2L));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void rejeitaEmailDuplicadoAntesDeConsultarUnidade() {
        when(usuarioRepository.existsByEmail("tecnico@teste.com")).thenReturn(true);
        UsuarioRequest request = new UsuarioRequest(
                5L, "Técnico", "TECNICO@TESTE.COM", TipoAcesso.TECNICO,
                "Técnico", "Senha123", LocalDate.of(2000, 1, 1));

        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(request, 2L));
        verifyNoInteractions(unidadeRepository);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void retornaNaoEncontradoQuandoUnidadeNaoExiste() {
        when(unidadeRepository.findById(99L)).thenReturn(Optional.empty());
        UsuarioRequest request = new UsuarioRequest(
                99L, "Técnico", "tecnico@teste.com", TipoAcesso.TECNICO,
                "Técnico", "Senha123", LocalDate.of(2000, 1, 1));

        assertThrows(EntityNotFoundException.class, () -> service.cadastrar(request, 2L));
    }
}
