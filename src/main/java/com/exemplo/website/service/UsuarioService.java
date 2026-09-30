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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EnderecoRepository enderecoRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            EnderecoRepository enderecoRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.enderecoRepository = enderecoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponse cadastrar(UsuarioRequest request) {

        validarTipoAcesso(request.tipoAcesso());
        validarCadastro(request);

        String email = normalizarEmail(request.email());

        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Já existe um usuário cadastrado com este email"
            );
        }

        Endereco endereco = enderecoRepository
                .findById(request.enderecoId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Endereço não encontrado"
                        )
                );

        Usuario usuario = new Usuario();

        usuario.setEndereco(endereco);
        usuario.setNomeCompleto(request.nomeCompleto().trim());
        usuario.setEmail(email);
        usuario.setTipoAcesso(request.tipoAcesso());

        usuario.setSenhaHash(
                passwordEncoder.encode(request.senha())
        );

        usuario.setDataNascimento(request.dataNascimento());

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return toResponse(usuarioSalvo);
    }

    public List<UsuarioResponse> listarTodos() {

        return usuarioRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UsuarioResponse buscarPorId(Long id) {

        Usuario usuario = buscarUsuario(id);

        return toResponse(usuario);
    }

    public UsuarioResponse atualizar(
            Long id,
            UsuarioUpdateRequest request
    ) {

        Usuario usuario = buscarUsuario(id);

        if (request.enderecoId() != null) {

            Endereco endereco = enderecoRepository
                    .findById(request.enderecoId())
                    .orElseThrow(() ->
                            new EntityNotFoundException(
                                    "Endereço não encontrado"
                            )
                    );

            usuario.setEndereco(endereco);
        }

        if (request.nomeCompleto() != null) {

            if (request.nomeCompleto().isBlank()) {
                throw new IllegalArgumentException(
                        "Nome não pode estar vazio"
                );
            }

            usuario.setNomeCompleto(
                    request.nomeCompleto().trim()
            );
        }

        if (request.email() != null) {

            if (request.email().isBlank()) {
                throw new IllegalArgumentException(
                        "Email não pode estar vazio"
                );
            }

            String email = normalizarEmail(request.email());

            if (usuarioRepository
                    .existsByEmailAndIdNot(email, id)) {

                throw new IllegalArgumentException(
                        "Já existe um usuário cadastrado com este email"
                );
            }

            usuario.setEmail(email);
        }

        if (request.tipoAcesso() != null) {

            validarTipoAcesso(request.tipoAcesso());

            usuario.setTipoAcesso(
                    request.tipoAcesso()
            );
        }

        if (request.dataNascimento() != null) {
            usuario.setDataNascimento(
                    request.dataNascimento()
            );
        }

        Usuario usuarioAtualizado =
                usuarioRepository.save(usuario);

        return toResponse(usuarioAtualizado);
    }

    public UsuarioResponse desativar(Long id) {

        Usuario usuario = buscarUsuario(id);

        usuario.setEstaAtivo(false);

        Usuario usuarioAtualizado =
                usuarioRepository.save(usuario);

        return toResponse(usuarioAtualizado);
    }

    public UsuarioResponse ativar(Long id) {

        Usuario usuario = buscarUsuario(id);

        usuario.setEstaAtivo(true);

        Usuario usuarioAtualizado =
                usuarioRepository.save(usuario);

        return toResponse(usuarioAtualizado);
    }

    private Usuario buscarUsuario(Long id) {

        return usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Usuário não encontrado"
                        )
                );
    }

    private void validarTipoAcesso(
            TipoAcesso tipoAcesso
    ) {

        if (tipoAcesso != TipoAcesso.GESTOR
                && tipoAcesso != TipoAcesso.TECNICO) {

            throw new IllegalArgumentException(
                    "Apenas gestores e técnicos podem ser cadastrados por este serviço"
            );
        }
    }

    private void validarCadastro(
            UsuarioRequest request
    ) {

        if (request.enderecoId() == null) {
            throw new IllegalArgumentException(
                    "Endereço é obrigatório"
            );
        }

        if (request.nomeCompleto() == null
                || request.nomeCompleto().isBlank()) {

            throw new IllegalArgumentException(
                    "Nome é obrigatório"
            );
        }

        if (request.email() == null
                || request.email().isBlank()) {

            throw new IllegalArgumentException(
                    "Email é obrigatório"
            );
        }

        if (request.senha() == null
                || request.senha().isBlank()) {

            throw new IllegalArgumentException(
                    "Senha é obrigatória"
            );
        }

        if (request.dataNascimento() == null) {
            throw new IllegalArgumentException(
                    "Data de nascimento é obrigatória"
            );
        }
    }

    private String normalizarEmail(String email) {

        return email
                .trim()
                .toLowerCase();
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
                usuario.getDataNascimento(),
                usuario.getDataCriacao(),
                usuario.getEstaAtivo(),
                usuario.getPrimeiroAcesso()
        );
    }
}