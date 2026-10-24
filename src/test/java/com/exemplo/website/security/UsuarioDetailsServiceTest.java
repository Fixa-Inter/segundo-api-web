package com.exemplo.website.security;

import com.exemplo.website.model.Unidade;
import com.exemplo.website.model.Enum.TipoAcesso;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private UsuarioDetailsService usuarioDetailsService;

    @BeforeEach
    void setUp() {
        usuarioDetailsService =
                new UsuarioDetailsService(usuarioRepository);
    }

    @Test
    void deveCarregarGestorPeloEmail() {

        Usuario gestor = new Usuario();

        gestor.setEmail("gestor@empresa.com");
        gestor.setSenhaHash("$2y$12$hash");
        gestor.setTipoAcesso(TipoAcesso.GESTOR);
        gestor.setEstaAtivo(true);

        when(
                usuarioRepository.findByEmailAndTipoAcesso(
                        "gestor@empresa.com",
                        TipoAcesso.GESTOR
                )
        ).thenReturn(Optional.of(gestor));

        UserDetails resultado =
                usuarioDetailsService.loadUserByUsername(
                        "gestor@empresa.com"
                );

        assertEquals(
                "gestor@empresa.com",
                resultado.getUsername()
        );

        assertEquals(
                "$2y$12$hash",
                resultado.getPassword()
        );

        verify(usuarioRepository)
                .findByEmailAndTipoAcesso(
                        "gestor@empresa.com",
                        TipoAcesso.GESTOR
                );
    }

    @Test
    void deveCarregarAdministradorPeloCnpj() {

        Unidade unidade = new Unidade();
        unidade.setCnpj("12345678000190");

        Usuario administrador = new Usuario();

        administrador.setUnidade(unidade);
        administrador.setSenhaHash("$2y$12$hash");
        administrador.setTipoAcesso(TipoAcesso.ADMINISTRADOR);
        administrador.setEstaAtivo(true);

        when(
                usuarioRepository.findByUnidade_CnpjAndTipoAcesso(
                        "12345678000190",
                        TipoAcesso.ADMINISTRADOR
                )
        ).thenReturn(Optional.of(administrador));

        UserDetails resultado =
                usuarioDetailsService.loadUserByUsername(
                        "12.345.678/0001-90"
                );

        assertEquals(
                "12345678000190",
                resultado.getUsername()
        );

        verify(usuarioRepository)
                .findByUnidade_CnpjAndTipoAcesso(
                        "12345678000190",
                        TipoAcesso.ADMINISTRADOR
                );
    }

    @Test
    void naoDeveCarregarTecnicoPeloEmail() {

        when(
                usuarioRepository.findByEmailAndTipoAcesso(
                        "tecnico@empresa.com",
                        TipoAcesso.GESTOR
                )
        ).thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> usuarioDetailsService.loadUserByUsername(
                        "tecnico@empresa.com"
                )
        );

        verify(usuarioRepository)
                .findByEmailAndTipoAcesso(
                        "tecnico@empresa.com",
                        TipoAcesso.GESTOR
                );
    }

    @Test
    void deveRejeitarIdentificadorInvalido() {

        assertThrows(
                UsernameNotFoundException.class,
                () -> usuarioDetailsService.loadUserByUsername(
                        "rafael123"
                )
        );

        verifyNoInteractions(usuarioRepository);
    }
}