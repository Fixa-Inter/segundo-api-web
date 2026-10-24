package com.exemplo.website.service;

import com.exemplo.website.dto.aptidao.AptidaoRequest;
import com.exemplo.website.dto.aptidao.AptidaoResponse;
import com.exemplo.website.model.Aptidao;
import com.exemplo.website.model.Enum.CategoriaProblema;
import com.exemplo.website.model.Unidade;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.repository.AptidaoRepository;
import com.exemplo.website.repository.UsuarioRepository;
import com.exemplo.website.security.AccessPolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AptidaoServiceTest {

    private final AptidaoRepository aptidaoRepository = mock(AptidaoRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final AccessPolicyService accessPolicyService = mock(AccessPolicyService.class);
    private final AptidaoService service = new AptidaoService(
            aptidaoRepository, usuarioRepository, accessPolicyService);

    @BeforeEach
    void setUp() {
        Unidade unidade = new Unidade();
        unidade.setId(4L);
        Usuario usuario = new Usuario();
        usuario.setId(10L);
        usuario.setUnidade(unidade);
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(aptidaoRepository.existsByUsuario_IdAndCategoriaProblema(
                10L, CategoriaProblema.ELETRICA)).thenReturn(false);
        when(aptidaoRepository.save(any(Aptidao.class))).thenAnswer(invocation -> {
            Aptidao aptidao = invocation.getArgument(0);
            aptidao.setId(35L);
            return aptidao;
        });
    }

    @Test
    void cadastraAptidaoComNotaNoIntervalo() {
        AptidaoResponse response = service.cadastrar(
                new AptidaoRequest(10L, CategoriaProblema.ELETRICA, new BigDecimal("8.5")), 2L);

        assertEquals(35L, response.id());
        assertEquals(10L, response.usuarioId());
        assertEquals(new BigDecimal("8.5"), response.nota());
        assertTrue(response.estaAtivo());
        verify(accessPolicyService).exigirAcessoUnidade(2L, 4L);
    }

    @Test
    void rejeitaNotaForaDoIntervaloAntesDeConsultarUsuario() {
        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(
                new AptidaoRequest(10L, CategoriaProblema.ELETRICA, new BigDecimal("10.1")), 2L));

        verifyNoInteractions(usuarioRepository, aptidaoRepository);
    }

    @Test
    void rejeitaCategoriaDuplicadaParaMesmoUsuario() {
        when(aptidaoRepository.existsByUsuario_IdAndCategoriaProblema(
                10L, CategoriaProblema.ELETRICA)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(
                new AptidaoRequest(10L, CategoriaProblema.ELETRICA, new BigDecimal("8.5")), 2L));

        verify(aptidaoRepository, never()).save(any());
    }
}
