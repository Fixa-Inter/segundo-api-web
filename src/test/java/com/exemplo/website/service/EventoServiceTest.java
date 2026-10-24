package com.exemplo.website.service;

import com.exemplo.website.dto.evento.EventoRequest;
import com.exemplo.website.dto.evento.EventoResponse;
import com.exemplo.website.model.Evento;
import com.exemplo.website.model.LocalEndereco;
import com.exemplo.website.model.Unidade;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.repository.EventoRepository;
import com.exemplo.website.repository.LocalEnderecoRepository;
import com.exemplo.website.repository.UsuarioRepository;
import com.exemplo.website.security.AccessPolicyService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventoServiceTest {

    private final EventoRepository eventoRepository = mock(EventoRepository.class);
    private final LocalEnderecoRepository localRepository = mock(LocalEnderecoRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final AccessPolicyService accessPolicyService = mock(AccessPolicyService.class);
    private final EventoService service = new EventoService(
            eventoRepository, localRepository, usuarioRepository, accessPolicyService);

    private Usuario usuario;
    private LocalEndereco local;

    @BeforeEach
    void setUp() {
        Unidade unidade = new Unidade();
        unidade.setId(4L);
        usuario = new Usuario();
        usuario.setId(10L);
        usuario.setUnidade(unidade);
        local = new LocalEndereco();
        local.setId(20L);
        local.setUnidade(unidade);
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(localRepository.findById(20L)).thenReturn(Optional.of(local));
        when(eventoRepository.save(any(Evento.class))).thenAnswer(invocation -> {
            Evento evento = invocation.getArgument(0);
            evento.setId(30L);
            return evento;
        });
    }

    @Test
    void cadastraEventoQuandoUsuarioELocalSaoDaMesmaUnidade() {
        LocalDateTime inicio = LocalDateTime.of(2026, 10, 21, 9, 0);
        EventoRequest request = new EventoRequest(
                10L, 20L, "  Manutenção  ", "  Descrição  ", "  Sala 1  ",
                null, inicio, inicio.plusHours(1));

        EventoResponse response = service.cadastrar(request, 2L);

        assertEquals(30L, response.id());
        assertEquals("Manutenção", response.titulo());
        assertEquals(10L, response.usuarioId());
        assertEquals(20L, response.localEnderecoId());
        verify(accessPolicyService, times(2)).exigirAcessoUnidade(2L, 4L);
        verify(eventoRepository).save(any(Evento.class));
    }

    @Test
    void rejeitaFimAnteriorOuIgualAoInicio() {
        LocalDateTime inicio = LocalDateTime.of(2026, 10, 21, 9, 0);
        EventoRequest request = new EventoRequest(
                10L, 20L, "Título", "Descrição", "Local", null,
                inicio, inicio);

        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(request, 2L));
        verifyNoInteractions(usuarioRepository, localRepository, eventoRepository);
    }

    @Test
    void rejeitaUsuarioELocalDeUnidadesDiferentes() {
        Unidade outraUnidade = new Unidade();
        outraUnidade.setId(8L);
        local.setUnidade(outraUnidade);
        LocalDateTime inicio = LocalDateTime.of(2026, 10, 21, 9, 0);
        EventoRequest request = new EventoRequest(
                10L, 20L, "Título", "Descrição", "Local", null,
                inicio, inicio.plusHours(1));

        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(request, 2L));
        verify(eventoRepository, never()).save(any());
    }

    @Test
    void informaQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        LocalDateTime inicio = LocalDateTime.of(2026, 10, 21, 9, 0);
        EventoRequest request = new EventoRequest(
                99L, 20L, "Título", "Descrição", "Local", null,
                inicio, inicio.plusHours(1));

        assertThrows(EntityNotFoundException.class, () -> service.cadastrar(request, 2L));
    }
}
