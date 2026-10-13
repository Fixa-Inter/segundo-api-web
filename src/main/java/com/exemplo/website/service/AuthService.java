package com.exemplo.website.service;

import com.exemplo.website.dto.auth.LoginRequest;
import com.exemplo.website.dto.auth.LoginResponse;
import com.exemplo.website.model.Endereco;
import com.exemplo.website.model.Enum.TipoAcesso;
import com.exemplo.website.model.Instituicao;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.repository.UsuarioRepository;
import com.exemplo.website.security.JwtService;
import com.exemplo.website.security.UsuarioPrincipal;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

@Service
public class AuthService {

    private static final Set<TipoAcesso> TIPOS_PERMITIDOS = Set.of(
            TipoAcesso.GESTOR,
            TipoAcesso.ADMINISTRADOR
    );

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        String email = request.email().trim();

        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow(AuthService::credenciaisInvalidas);

        validarTipoAcesso(usuario);
        validarSenha(request.senha(), usuario.getSenhaHash());
        validarCnpj(request.cnpj(), usuario.getEndereco());
        validarDominio(email, usuario.getEndereco());
        validarUsuarioAtivo(usuario);

        String token = jwtService.gerarToken(new UsuarioPrincipal(usuario));

        return new LoginResponse(
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getTipoAcesso().name(),
                token
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

    private void validarCnpj(String cnpjInformado, Endereco endereco) {
        String cnpjUsuario = endereco == null
                ? null
                : normalizarCnpj(endereco.getCnpj());

        if (cnpjUsuario == null
                || !cnpjUsuario.equals(normalizarCnpj(cnpjInformado))) {
            throw credenciaisInvalidas();
        }
    }

    private void validarDominio(String email, Endereco endereco) {
        Instituicao instituicao = endereco == null
                ? null
                : endereco.getInstituicao();

        String dominioInstituicao = instituicao == null
                ? null
                : normalizarDominio(instituicao.getDominioEmail());

        int separador = email.lastIndexOf('@');
        String dominioEmail = separador > 0 && separador < email.length() - 1
                ? email.substring(separador + 1).toLowerCase(Locale.ROOT)
                : null;

        if (dominioInstituicao == null
                || !dominioInstituicao.equals(dominioEmail)) {
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

    private String normalizarDominio(String dominio) {
        if (dominio == null) {
            return null;
        }

        String normalizado = dominio.trim().toLowerCase(Locale.ROOT);
        return normalizado.startsWith("@")
                ? normalizado.substring(1)
                : normalizado;
    }

    private static BadCredentialsException credenciaisInvalidas() {
        return new BadCredentialsException("Credenciais inválidas");
    }
}
