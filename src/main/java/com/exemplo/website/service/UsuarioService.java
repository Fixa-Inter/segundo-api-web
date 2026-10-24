package com.exemplo.website.service;

import com.exemplo.website.dto.usuario.UsuarioRequest;
import com.exemplo.website.dto.usuario.UsuarioResponse;
import com.exemplo.website.dto.usuario.UsuarioUpdateRequest;
import com.exemplo.website.model.Unidade;
import com.exemplo.website.model.Enum.TipoAcesso;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.repository.UnidadeRepository;
import com.exemplo.website.repository.UsuarioRepository;
import com.exemplo.website.security.AccessPolicyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioService {

    private final UnidadeRepository unidadeRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final AccessPolicyService accessPolicyService;

    public UsuarioResponse cadastrar(
            UsuarioRequest request,
            Long usuarioAutenticadoId
    ) {

        validarTipoCadastro(request.tipoAcesso());

        String email = normalizarEmail(request.email());

        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Já existe um usuário cadastrado com esse email"
            );
        }

        Unidade unidade =
                unidadeRepository
                        .findById(request.unidadeId())
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Unidade não encontrada"
                                )
                        );

        accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, unidade.getId());
        if (!accessPolicyService.ehAdministrador(usuarioAutenticadoId)
                && request.tipoAcesso() != TipoAcesso.TECNICO) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "O gestor só pode cadastrar técnicos"
            );
        }
        EmailInstitucionalValidator.validar(email, unidade);

        Usuario usuario = new Usuario();

        usuario.setUnidade(unidade);
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

    public List<UsuarioResponse> listarTodos(
            Long usuarioAutenticadoId,
            Long unidadeSolicitada,
            TipoAcesso tipoSolicitado
    ) {

        if (!accessPolicyService.ehAdministrador(usuarioAutenticadoId)) {
            Long unidadeGestor = accessPolicyService.unidadeDoUsuarioAutenticado(usuarioAutenticadoId);
            if (unidadeSolicitada != null && !unidadeGestor.equals(unidadeSolicitada)) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "O gestor só pode listar usuários da própria unidade"
                );
            }
            if (tipoSolicitado != null && tipoSolicitado != TipoAcesso.TECNICO) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "O gestor só pode listar técnicos"
                );
            }
            return usuarioRepository.findByUnidade_Id(unidadeGestor)
                    .stream()
                    .filter(usuario -> usuario.getTipoAcesso() == TipoAcesso.TECNICO)
                    .map(this::toResponse)
                    .toList();
        }

        if (unidadeSolicitada != null) {
            accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, unidadeSolicitada);
        }
        List<Usuario> usuarios = unidadeSolicitada == null
                ? usuarioRepository.findAll()
                : usuarioRepository.findByUnidade_Id(unidadeSolicitada);
        return usuarios
                .stream()
                .filter(usuario -> tipoSolicitado == null || usuario.getTipoAcesso() == tipoSolicitado)
                .map(this::toResponse)
                .toList();
    }

    public List<UsuarioResponse> listarPorUnidade(
            Long unidadeId
    ) {

        return usuarioRepository
                .findByUnidade_Id(unidadeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<UsuarioResponse> listarTecnicos(
            Long unidadeId
    ) {

        return usuarioRepository
                .findByUnidade_IdAndTipoAcesso(
                        unidadeId,
                        TipoAcesso.TECNICO
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UsuarioResponse buscarPorId(Long id, Long usuarioAutenticadoId) {
        Usuario usuario = buscarEntidade(id);
        accessPolicyService.exigirAcessoUsuario(usuarioAutenticadoId, usuario);
        return toResponse(usuario);
    }

    public UsuarioResponse atualizar(
            Long id,
            UsuarioUpdateRequest request,
            Long usuarioAutenticadoId
    ) {

        Usuario usuario = buscarEntidade(id);
        accessPolicyService.exigirAcessoUsuario(usuarioAutenticadoId, usuario);

        if (request.unidadeId() != null) {

            Unidade unidade =
                    unidadeRepository
                            .findById(request.unidadeId())
                            .orElseThrow(() ->
                                    new EntityNotFoundException(
                                            "Unidade não encontrada"
                                    )
                            );

            accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, unidade.getId());
            EmailInstitucionalValidator.validar(usuario.getEmail(), unidade);

            usuario.setUnidade(unidade);
        }

        if (request.nomeCompleto() != null) {
            usuario.setNomeCompleto(
                    request.nomeCompleto().trim()
            );
        }

        if (request.email() != null) {

            String email =
                    normalizarEmail(request.email());

            EmailInstitucionalValidator.validar(email, usuario.getUnidade());

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

            if (!accessPolicyService.ehAdministrador(usuarioAutenticadoId)
                    && request.tipoAcesso() != TipoAcesso.TECNICO) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "O gestor não pode alterar o tipo de acesso para gestor"
                );
            }

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

    public UsuarioResponse ativar(Long id, Long usuarioAutenticadoId) {

        Usuario usuario = buscarEntidade(id);
        accessPolicyService.exigirAcessoUsuario(usuarioAutenticadoId, usuario);

        usuario.setEstaAtivo(true);

        return toResponse(
                usuarioRepository.save(usuario)
        );
    }

    public UsuarioResponse desativar(Long id, Long usuarioAutenticadoId) {

        Usuario usuario = buscarEntidade(id);
        accessPolicyService.exigirAcessoUsuario(usuarioAutenticadoId, usuario);

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
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private UsuarioResponse toResponse(
            Usuario usuario
    ) {

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getUnidade().getId(),
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
