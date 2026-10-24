package com.exemplo.website.service;

import com.exemplo.website.dto.auth.LoginRequest;
import com.exemplo.website.dto.auth.LoginResponse;
import com.exemplo.website.dto.auth.AlterarSenhaRequest;
import com.exemplo.website.model.Unidade;
import com.exemplo.website.model.Enum.TipoAcesso;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.repository.UsuarioRepository;
import com.exemplo.website.repository.RefreshTokenRepository;
import com.exemplo.website.security.JwtService;
import com.exemplo.website.security.UsuarioPrincipal;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;

@Service
public class AuthService {

    private static final Set<TipoAcesso> TIPOS_PERMITIDOS = Set.of(
            TipoAcesso.GESTOR,
            TipoAcesso.ADMINISTRADOR
    );

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Duration refreshTokenValidity;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenRepository refreshTokenRepository,
            @Value("${security.jwt.refresh-expiration-seconds:2592000}") long refreshTokenExpirationSeconds
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenValidity = Duration.ofSeconds(refreshTokenExpirationSeconds);
    }

    public LoginResponse login(LoginRequest request) {
        String email = EmailInstitucionalValidator.normalizar(request.email());

        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow(AuthService::credenciaisInvalidas);

        validarTipoAcesso(usuario);
        validarSenha(request.senha(), usuario.getSenhaHash());
        validarCnpj(request.cnpj(), usuario.getUnidade());
        try {
            EmailInstitucionalValidator.validar(email, usuario.getUnidade());
        } catch (IllegalArgumentException exception) {
            throw credenciaisInvalidas();
        }
        validarUsuarioAtivo(usuario);

        return emitirTokens(usuario);
    }

    public LoginResponse renovar(String refreshToken) {
        Long usuarioId = refreshTokenRepository.consumir(refreshToken)
                .orElseThrow(AuthService::credenciaisInvalidas);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(AuthService::credenciaisInvalidas);

        if (!TIPOS_PERMITIDOS.contains(usuario.getTipoAcesso())
                || !Boolean.TRUE.equals(usuario.getEstaAtivo())) {
            throw credenciaisInvalidas();
        }

        return emitirTokens(usuario);
    }

    public void logout(String refreshToken) {
        refreshTokenRepository.remover(refreshToken);
    }

    @Transactional
    public void alterarSenha(Long usuarioId, AlterarSenhaRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(AuthService::credenciaisInvalidas);
        validarUsuarioAtivo(usuario);
        if (usuario.getSenhaHash() == null
                || !passwordEncoder.matches(request.senhaAtual(), usuario.getSenhaHash())) {
            throw credenciaisInvalidas();
        }
        if (!request.novaSenha().equals(request.confirmarNovaSenha())) {
            throw new IllegalArgumentException("A confirmação da nova senha não confere");
        }

        usuario.setSenhaHash(passwordEncoder.encode(request.novaSenha()));
        usuario.setPrimeiroAcesso(false);
        usuarioRepository.save(usuario);
        refreshTokenRepository.removerTodosPorUsuarioId(usuarioId);
    }

    private LoginResponse emitirTokens(Usuario usuario) {
        UsuarioPrincipal principal = new UsuarioPrincipal(usuario);
        String accessToken = jwtService.gerarToken(principal);
        String refreshToken = jwtService.gerarRefreshToken();
        refreshTokenRepository.salvar(refreshToken, usuario.getId(), refreshTokenValidity);

        return new LoginResponse(
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getTipoAcesso().name(),
                accessToken,
                refreshToken
        );
    }

    private void validarTipoAcesso(Usuario usuario) {
        if (!TIPOS_PERMITIDOS.contains(usuario.getTipoAcesso())) {
            throw credenciaisInvalidas();
        }
    }

    private void validarSenha(String senha, String senhaHash) {
        if (senhaHash == null || !passwordEncoder.matches(senha, senhaHash)) {
            throw credenciaisInvalidas();
        }
    }

    private void validarCnpj(String cnpjInformado, Unidade unidade) {
        String cnpjUsuario = unidade == null
                ? null
                : normalizarCnpj(unidade.getCnpj());

        if (cnpjUsuario == null
                || !cnpjUsuario.equals(normalizarCnpj(cnpjInformado))) {
            throw credenciaisInvalidas();
        }
    }

    private void validarUsuarioAtivo(Usuario usuario) {
        if (!Boolean.TRUE.equals(usuario.getEstaAtivo())) {
            throw credenciaisInvalidas();
        }
    }

    private String normalizarCnpj(String cnpj) {
        if (cnpj == null) {
            return null;
        }

        String normalizado = cnpj.replaceAll("\\D", "");
        return normalizado.isBlank() ? null : normalizado;
    }

    private static BadCredentialsException credenciaisInvalidas() {
        return new BadCredentialsException("Credenciais inválidas");
    }
}
