package com.exemplo.website.service;

import com.exemplo.website.dto.foto.FotoResponse;
import com.exemplo.website.dto.perfil.PerfilResponse;
import com.exemplo.website.dto.perfil.PerfilUpdateRequest;
import com.exemplo.website.model.Foto;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.repository.FotoRepository;
import com.exemplo.website.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final UsuarioRepository usuarioRepository;
    private final FotoRepository fotoRepository;

    public PerfilResponse buscarPorUsuarioId(Long usuarioId) {
        return toResponse(buscarUsuario(usuarioId));
    }

    public PerfilResponse atualizar(Long usuarioId, PerfilUpdateRequest request) {
        Usuario usuario = buscarUsuario(usuarioId);

        if (request.nomeCompleto() != null) {
            usuario.setNomeCompleto(request.nomeCompleto().trim());
        }

        if (request.email() != null) {
            String email = request.email().trim().toLowerCase();

            if (usuarioRepository.existsByEmailAndIdNot(email, usuarioId)) {
                throw new IllegalArgumentException(
                        "Já existe um usuário cadastrado com esse email"
                );
            }

            usuario.setEmail(email);
        }

        if (request.dataNascimento() != null) {
            usuario.setDataNascimento(request.dataNascimento());
        }

        return toResponse(usuarioRepository.save(usuario));
    }

    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    private PerfilResponse toResponse(Usuario usuario) {
        FotoResponse fotoResponse = fotoRepository
                .findByUsuario_Id(usuario.getId())
                .map(this::toFotoResponse)
                .orElse(null);

        return new PerfilResponse(
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.getTipoAcesso(),
                usuario.getCargo(),
                usuario.getDataNascimento(),
                fotoResponse
        );
    }

    private FotoResponse toFotoResponse(Foto foto) {
        return new FotoResponse(foto.getId(), foto.getUrl());
    }
}
