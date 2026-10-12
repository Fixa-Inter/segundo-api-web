package com.exemplo.website.service;

import com.exemplo.website.dto.usuario.UsuarioRequest;
import com.exemplo.website.dto.usuario.UsuarioResponse;
import com.exemplo.website.dto.usuario.UsuarioUpdateRequest;
import com.exemplo.website.model.Endereco;
import com.exemplo.website.model.Enum.TipoAcesso;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.repository.EnderecoRepository;
import com.exemplo.website.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final EnderecoRepository enderecoRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    public UsuarioResponse cadastrar(
            UsuarioRequest request
    ) {

        validarTipoCadastro(request.tipoAcesso());

        String email = normalizarEmail(request.email());

        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Já existe um usuário cadastrado com esse email"
            );
        }

        Endereco endereco =
                enderecoRepository
                        .findById(request.enderecoId())
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Endereço não encontrado"
                                )
                        );

        Usuario usuario = new Usuario();

        usuario.setEndereco(endereco);
        usuario.setNomeCompleto(
                request.nomeCompleto().trim()
        );
        usuario.setEmail(email);
        usuario.setTipoAcesso(
                request.tipoAcesso()
        );
        usuario.setCargo(
                request.cargo().trim()
        );
        usuario.setSenhaHash(
                passwordEncoder.encode(request.senha())
        );
        usuario.setDataNascimento(
                request.dataNascimento()
        );

        return toResponse(
                usuarioRepository.save(usuario)
        );
    }

    public List<UsuarioResponse> listarTodos() {

        return usuarioRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<UsuarioResponse> listarPorEndereco(
            Long enderecoId
    ) {

        return usuarioRepository
                .findByEndereco_Id(enderecoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<UsuarioResponse> listarTecnicos(
            Long enderecoId
    ) {

        return usuarioRepository
                .findByEndereco_IdAndTipoAcesso(
                        enderecoId,
                        TipoAcesso.TECNICO
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UsuarioResponse buscarPorId(Long id) {
        return toResponse(buscarEntidade(id));
    }

    public UsuarioResponse atualizar(
            Long id,
            UsuarioUpdateRequest request
    ) {

        Usuario usuario = buscarEntidade(id);

        if (request.enderecoId() != null) {

            Endereco endereco =
                    enderecoRepository
                            .findById(request.enderecoId())
                            .orElseThrow(() ->
                                    new EntityNotFoundException(
                                            "Endereço não encontrado"
                                    )
                            );

            usuario.setEndereco(endereco);
        }

        if (request.nomeCompleto() != null) {
            usuario.setNomeCompleto(
                    request.nomeCompleto().trim()
            );
        }

        if (request.email() != null) {

            String email =
                    normalizarEmail(request.email());

            if (
                    usuarioRepository
                            .existsByEmailAndIdNot(
                                    email,
                                    id
                            )
            ) {
                throw new IllegalArgumentException(
                        "Já existe um usuário cadastrado com esse email"
                );
            }

            usuario.setEmail(email);
        }

        if (request.tipoAcesso() != null) {

            validarTipoCadastro(
                    request.tipoAcesso()
            );

            usuario.setTipoAcesso(
                    request.tipoAcesso()
            );
        }

        if (request.cargo() != null) {
            usuario.setCargo(
                    request.cargo().trim()
            );
        }

        if (request.dataNascimento() != null) {
            usuario.setDataNascimento(
                    request.dataNascimento()
            );
        }

        return toResponse(
                usuarioRepository.save(usuario)
        );
    }

    public UsuarioResponse ativar(Long id) {

        Usuario usuario = buscarEntidade(id);

        usuario.setEstaAtivo(true);

        return toResponse(
                usuarioRepository.save(usuario)
        );
    }

    public UsuarioResponse desativar(Long id) {

        Usuario usuario = buscarEntidade(id);

        usuario.setEstaAtivo(false);

        return toResponse(
                usuarioRepository.save(usuario)
        );
    }

    private Usuario buscarEntidade(Long id) {

        return usuarioRepository
                .findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Usuário não encontrado"
                        )
                );
    }

    private void validarTipoCadastro(
            TipoAcesso tipoAcesso
    ) {

        if (
                tipoAcesso != TipoAcesso.GESTOR &&
                        tipoAcesso != TipoAcesso.TECNICO
        ) {
            throw new IllegalArgumentException(
                    "A API web permite cadastrar apenas GESTOR ou TECNICO"
            );
        }
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }

    private UsuarioResponse toResponse(
            Usuario usuario
    ) {

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getEndereco().getId(),
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.getTipoAcesso(),
                usuario.getCargo(),
                usuario.getDataNascimento(),
                usuario.getDataCriacao(),
                usuario.getEstaAtivo(),
                usuario.getPrimeiroAcesso()
        );
    }
}