package com.exemplo.website.security;

import com.exemplo.website.model.Enum.TipoAcesso;
import com.exemplo.website.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UsuarioPrincipal implements UserDetails {

    private final Usuario usuario;

    public UsuarioPrincipal(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = "ROLE_" + usuario.getTipoAcesso().name();

        return List.of(
                new SimpleGrantedAuthority(role)
        );
    }

    @Override
    public String getPassword() {
        return usuario.getSenhaHash();
    }

    @Override
    public String getUsername() {
        if (usuario.getTipoAcesso() == TipoAcesso.ADMINISTRADOR) {
            return usuario.getEndereco().getCnpj();
        }

        return usuario.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(usuario.getEstaAtivo());
    }

    public Usuario getUsuario() {
        return usuario;
    }
}