package com.exemplo.website.service;

import com.exemplo.website.dto.auth.ConfirmarRecuperacaoSenhaRequest;
import com.exemplo.website.dto.auth.SolicitarRecuperacaoSenhaRequest;
import com.exemplo.website.model.RecuperarSenhaCodigo;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.repository.RecuperarSenhaCodigoRepository;
import com.exemplo.website.repository.UsuarioRepository;
import com.exemplo.website.repository.RefreshTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RecuperacaoSenhaService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final RecuperarSenhaCodigoRepository codigoRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RefreshTokenRepository refreshTokenRepository;

    public void solicitar(SolicitarRecuperacaoSenhaRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        usuarioRepository.findByEmail(email)
                .filter(usuario -> Boolean.TRUE.equals(usuario.getEstaAtivo()))
                .ifPresent(this::gerarEEnviarCodigo);
    }

    @Transactional
    public void confirmar(ConfirmarRecuperacaoSenhaRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .filter(u -> Boolean.TRUE.equals(u.getEstaAtivo()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Código inválido ou expirado"
                ));

        RecuperarSenhaCodigo codigoSalvo = codigoRepository
                .buscarPorUsuarioId(usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Código inválido ou expirado"
                ));

        if (!codigoSalvo.getCodigo().equals(request.codigo())) {
            throw new IllegalArgumentException("Código inválido ou expirado");
        }

        usuario.setSenhaHash(passwordEncoder.encode(request.novaSenha()));
        usuario.setPrimeiroAcesso(false);
        usuarioRepository.save(usuario);
        refreshTokenRepository.removerTodosPorUsuarioId(usuario.getId());
        codigoRepository.removerPorUsuarioId(usuario.getId());
    }

    private void gerarEEnviarCodigo(Usuario usuario) {
        int codigo = RANDOM.nextInt(100_000, 1_000_000);
        codigoRepository.salvar(new RecuperarSenhaCodigo(usuario.getId(), codigo));
        emailService.enviarCodigoRecuperacao(
                usuario.getEmail(), usuario.getNomeCompleto(), codigo
        );
    }
}
