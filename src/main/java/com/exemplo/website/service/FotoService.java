package com.exemplo.website.service;

import com.exemplo.website.dto.foto.FotoRequest;
import com.exemplo.website.dto.foto.FotoResponse;
import com.exemplo.website.model.Foto;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.repository.FotoRepository;
import com.exemplo.website.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class FotoService {

    private final FotoRepository fotoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public FotoResponse buscarPorUsuarioId(Long usuarioId) {
        validarUsuarioAtivo(usuarioId);
        return fotoRepository.findFirstByUsuario_IdOrderByIdDesc(usuarioId)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Foto de perfil não encontrada"
                ));
    }

    @Transactional
    public FotoResponse salvarOuAtualizar(Long usuarioId, FotoRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuário não encontrado"
                ));
        validarAtivo(usuario);

        String url = validarUrl(request.url());
        Foto foto = fotoRepository.findFirstByUsuario_IdOrderByIdDesc(usuarioId)
                .orElseGet(() -> {
                    Foto novaFoto = new Foto();
                    novaFoto.setUsuario(usuario);
                    return novaFoto;
                });

        foto.setUrl(url);
        return toResponse(fotoRepository.save(foto));
    }

    @Transactional
    public void removerPorUsuarioId(Long usuarioId) {
        validarUsuarioAtivo(usuarioId);
        Foto foto = fotoRepository.findFirstByUsuario_IdOrderByIdDesc(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Foto de perfil não encontrada"
                ));

        fotoRepository.delete(foto);
    }

    private void validarUsuarioAtivo(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        validarAtivo(usuario);
    }

    private void validarAtivo(Usuario usuario) {
        if (!Boolean.TRUE.equals(usuario.getEstaAtivo())) {
            throw new AccessDeniedException("Usuário inativo");
        }
    }

    private String validarUrl(String valor) {
        String url = valor.trim();
        try {
            URI uri = new URI(url);
            String esquema = uri.getScheme();
            boolean http = esquema != null
                    && (esquema.toLowerCase(Locale.ROOT).equals("http")
                    || esquema.toLowerCase(Locale.ROOT).equals("https"));

            if (!uri.isAbsolute() || !http || uri.getHost() == null) {
                throw new IllegalArgumentException(
                        "A URL da foto deve ser um endereço HTTP ou HTTPS válido"
                );
            }
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException(
                    "A URL da foto é inválida",
                    exception
            );
        }

        return url;
    }

    private FotoResponse toResponse(Foto foto) {
        return new FotoResponse(foto.getId(), foto.getUrl());
    }
}
