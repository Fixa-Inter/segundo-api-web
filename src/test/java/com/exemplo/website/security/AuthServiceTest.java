package com.exemplo.website.security;

import com.exemplo.website.dto.auth.LoginRequest;
import com.exemplo.website.dto.auth.LoginResponse;
import com.exemplo.website.model.Instituicao;
import com.exemplo.website.model.Enum.TipoAcesso;
import com.exemplo.website.model.Unidade;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.repository.UsuarioRepository;
import com.exemplo.website.repository.RefreshTokenRepository;
import com.exemplo.website.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthService authService = new AuthService(
            usuarioRepository,
            passwordEncoder,
            jwtService,
            refreshTokenRepository,
            2592000
    );

    private Usuario gestor;

    @BeforeEach
    void setUp() {
        Instituicao instituicao = new Instituicao();
        instituicao.setDominioEmail("gmail.com");

        Unidade unidade = new Unidade();
        unidade.setCnpj("12000000007919");
        unidade.setInstituicao(instituicao);

        gestor = new Usuario();
        gestor.setId(147L);
        gestor.setUnidade(unidade);
        gestor.setNomeCompleto("Gestor de Teste");
        gestor.setEmail("raphamendesy@gmail.com");
        gestor.setTipoAcesso(TipoAcesso.GESTOR);
        gestor.setSenhaHash(passwordEncoder.encode("Senha123"));
        gestor.setDataNascimento(LocalDate.of(2000, 8, 11));
        gestor.setEstaAtivo(true);

        when(usuarioRepository.findByEmail(gestor.getEmail()))
                .thenReturn(Optional.of(gestor));
        when(jwtService.gerarToken(any(UsuarioPrincipal.class)))
                .thenReturn("test-token");
        when(jwtService.gerarRefreshToken()).thenReturn("test-refresh-token");
    }

    @Test
    void logsInWithPlainPasswordAndReturnsJwt() {
        LoginResponse response = authService.login(new LoginRequest(
                "  raphamendesy@gmail.com  ",
                "Senha123",
                "12.000.000/0079-19"
        ));

        assertEquals(147L, response.usuarioId());
        assertEquals("GESTOR", response.tipoAcesso());
        assertEquals("test-token", response.token());
        assertEquals("test-refresh-token", response.refreshToken());
        org.mockito.Mockito.verify(refreshTokenRepository).salvar(
                "test-refresh-token", 147L, Duration.ofSeconds(2592000)
        );
    }

    @Test
    void rejectsPlainTextStoredInPasswordHashColumn() {
        gestor.setSenhaHash("1234");

        assertThrows(BadCredentialsException.class, () -> authService.login(
                new LoginRequest(
                        "raphamendesy@gmail.com",
                        "1234",
                        "12000000007919"
                )
        ));
    }

    @Test
    void rejectsTechnicianRoleForThisLoginFlow() {
        gestor.setTipoAcesso(TipoAcesso.TECNICO);

        assertThrows(BadCredentialsException.class, () -> authService.login(
                new LoginRequest(
                        "raphamendesy@gmail.com",
                        "Senha123",
                        "12000000007919"
                )
        ));
    }

    @Test
    void refreshConsumesOldTokenAndIssuesReplacement() {
        when(refreshTokenRepository.consumir("old-refresh-token"))
                .thenReturn(Optional.of(147L));
        when(usuarioRepository.findById(147L)).thenReturn(Optional.of(gestor));

        LoginResponse response = authService.renovar("old-refresh-token");

        assertEquals("test-token", response.token());
        assertEquals("test-refresh-token", response.refreshToken());
        verify(refreshTokenRepository).consumir("old-refresh-token");
        verify(refreshTokenRepository).salvar(
                "test-refresh-token", 147L, Duration.ofSeconds(2592000)
        );
    }

    @Test
    void rejectsRefreshTokenThatWasAlreadyConsumedOrDoesNotExist() {
        when(refreshTokenRepository.consumir("used-refresh-token"))
                .thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> authService.renovar("used-refresh-token"));
    }

    @Test
    void logoutRevokesRefreshToken() {
        authService.logout("refresh-token-to-revoke");

        verify(refreshTokenRepository).remover("refresh-token-to-revoke");
    }
}
