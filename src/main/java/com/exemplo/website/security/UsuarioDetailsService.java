package com.exemplo.website.security;

import com.exemplo.website.model.Enum.TipoAcesso;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identificador)
            throws UsernameNotFoundException {

        if (identificador == null || identificador.isBlank()) {
            throw new UsernameNotFoundException("Credenciais inválidas");
        }

        String identificadorNormalizado = identificador.trim();

        Usuario usuario;

        if (ehEmail(identificadorNormalizado)) {
            usuario = buscarGestorPorEmail(identificadorNormalizado);
        } else if (ehCnpj(identificadorNormalizado)) {
            usuario = buscarAdministradorPorCnpj(identificadorNormalizado);
        } else {
            throw new UsernameNotFoundException("Credenciais inválidas");
        }

        return new UsuarioPrincipal(usuario);
    }

    private Usuario buscarGestorPorEmail(String email) {
        return usuarioRepository
                .findByEmailAndTipoAcesso(email, TipoAcesso.GESTOR)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Credenciais inválidas")
                );
    }

    private Usuario buscarAdministradorPorCnpj(String cnpj) {
        String cnpjNormalizado = normalizarCnpj(cnpj);

        return usuarioRepository
                .findByEndereco_CnpjAndTipoAcesso(
                        cnpjNormalizado,
                        TipoAcesso.ADMINISTRADOR
                )
                .orElseThrow(
                        () -> new UsernameNotFoundException("Credenciais inválidas")
                );
    }

    private boolean ehEmail(String identificador) {
        return identificador.contains("@");
    }

    private boolean ehCnpj(String identificador) {
        return normalizarCnpj(identificador).matches("\\d{14}");
    }

    private String normalizarCnpj(String cnpj) {
        return cnpj.replaceAll("\\D", "");
    }
}