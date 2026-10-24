package com.exemplo.website.security;

import com.exemplo.website.model.Enum.TipoAcesso;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AccessPolicyService {

    private final UsuarioRepository usuarioRepository;

    public Usuario usuarioAutenticado(Long usuarioAutenticadoId) {
        Usuario usuarioAutenticado = usuarioRepository.findById(usuarioAutenticadoId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário autenticado não encontrado"));
        if (!Boolean.TRUE.equals(usuarioAutenticado.getEstaAtivo())) {
            throw new AccessDeniedException("Usuário inativo");
        }
        return usuarioAutenticado;
    }

    public boolean ehAdministrador(Long usuarioAutenticadoId) {
        return usuarioAutenticado(usuarioAutenticadoId).getTipoAcesso() == TipoAcesso.ADMINISTRADOR;
    }

    public Long unidadeDoUsuarioAutenticado(Long usuarioAutenticadoId) {
        Usuario usuarioAutenticado = usuarioAutenticado(usuarioAutenticadoId);
        if (usuarioAutenticado.getTipoAcesso() != TipoAcesso.GESTOR || usuarioAutenticado.getUnidade() == null) {
            throw new AccessDeniedException("Acesso permitido somente ao gestor da unidade");
        }
        return usuarioAutenticado.getUnidade().getId();
    }

    public void exigirAcessoUnidade(Long usuarioAutenticadoId, Long unidadeId) {
        if (ehAdministrador(usuarioAutenticadoId)) {
            return;
        }

        Long unidadeGestor = unidadeDoUsuarioAutenticado(usuarioAutenticadoId);
        if (!Objects.equals(unidadeGestor, unidadeId)) {
            throw new AccessDeniedException("O gestor só pode acessar dados da própria unidade");
        }
    }

    public void exigirAcessoUsuario(Long usuarioAutenticadoId, Usuario usuarioAlvo) {
        if (ehAdministrador(usuarioAutenticadoId)) {
            return;
        }

        exigirAcessoUnidade(
                usuarioAutenticadoId,
                usuarioAlvo.getUnidade() == null ? null : usuarioAlvo.getUnidade().getId()
        );
        if (usuarioAlvo.getTipoAcesso() != TipoAcesso.TECNICO) {
            throw new AccessDeniedException("O gestor só pode administrar técnicos da própria unidade");
        }
    }
}
