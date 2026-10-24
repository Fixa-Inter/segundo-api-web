package com.exemplo.website.service;

import com.exemplo.website.dto.localendereco.LocalEnderecoRequest;
import com.exemplo.website.dto.localendereco.LocalEnderecoResponse;
import com.exemplo.website.dto.localendereco.LocalEnderecoUpdateRequest;
import com.exemplo.website.model.Unidade;
import com.exemplo.website.model.LocalEndereco;
import com.exemplo.website.repository.UnidadeRepository;
import com.exemplo.website.repository.LocalEnderecoRepository;
import com.exemplo.website.security.AccessPolicyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LocalEnderecoService {

    private final UnidadeRepository unidadeRepository;
    private final LocalEnderecoRepository localEnderecoRepository;
    private final AccessPolicyService accessPolicyService;

    public LocalEnderecoResponse cadastrar(
            LocalEnderecoRequest request,
            Long usuarioAutenticadoId
    ) {

        Unidade unidade =
                unidadeRepository
                        .findById(request.unidadeId())
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Unidade não encontrada"
                                )
                        );

        accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, unidade.getId());

        LocalEndereco local = new LocalEndereco();

        local.setUnidade(unidade);
        local.setNome(request.nome().trim());
        local.setTipoLocalEndereco(
                request.tipoLocalEndereco()
        );
        local.setDescricao(
                request.descricao().trim()
        );

        return toResponse(
                localEnderecoRepository.save(local)
        );
    }

    public List<LocalEnderecoResponse> listarTodos(Long usuarioAutenticadoId) {
        List<LocalEndereco> locais = accessPolicyService.ehAdministrador(usuarioAutenticadoId)
                ? localEnderecoRepository.findAll()
                : localEnderecoRepository.findByUnidade_Id(accessPolicyService.unidadeDoUsuarioAutenticado(usuarioAutenticadoId));
        return locais
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<LocalEnderecoResponse> listarPorUnidade(
            Long unidadeId,
            Long usuarioAutenticadoId
    ) {

        accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, unidadeId);

        return localEnderecoRepository
                .findByUnidade_Id(unidadeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public LocalEnderecoResponse buscarPorId(Long id, Long usuarioAutenticadoId) {
        LocalEndereco local = buscarEntidade(id);
        validarAcesso(usuarioAutenticadoId, local);
        return toResponse(local);
    }

    public LocalEnderecoResponse atualizar(
            Long id,
            LocalEnderecoUpdateRequest request,
            Long usuarioAutenticadoId
    ) {

        LocalEndereco local =
                buscarEntidade(id);
        validarAcesso(usuarioAutenticadoId, local);

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

            local.setUnidade(unidade);
        }

        if (request.nome() != null) {
            local.setNome(request.nome().trim());
        }

        if (request.tipoLocalEndereco() != null) {
            local.setTipoLocalEndereco(
                    request.tipoLocalEndereco()
            );
        }

        if (request.descricao() != null) {
            local.setDescricao(
                    request.descricao().trim()
            );
        }

        return toResponse(
                localEnderecoRepository.save(local)
        );
    }

    public LocalEnderecoResponse ativar(Long id, Long usuarioAutenticadoId) {

        LocalEndereco local =
                buscarEntidade(id);
        validarAcesso(usuarioAutenticadoId, local);

        local.setEstaAtivo(true);

        return toResponse(
                localEnderecoRepository.save(local)
        );
    }

    public LocalEnderecoResponse desativar(Long id, Long usuarioAutenticadoId) {

        LocalEndereco local =
                buscarEntidade(id);
        validarAcesso(usuarioAutenticadoId, local);

        local.setEstaAtivo(false);

        return toResponse(
                localEnderecoRepository.save(local)
        );
    }

    private LocalEndereco buscarEntidade(Long id) {

        return localEnderecoRepository
                .findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Local não encontrado"
                        )
                );
    }

    private void validarAcesso(Long usuarioAutenticadoId, LocalEndereco local) {
        accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, local.getUnidade().getId());
    }

    private LocalEnderecoResponse toResponse(
            LocalEndereco local
    ) {

        return new LocalEnderecoResponse(
                local.getId(),
                local.getUnidade().getId(),
                local.getNome(),
                local.getTipoLocalEndereco(),
                local.getDescricao(),
                local.getDataCriacao(),
                local.getEstaAtivo()
        );
    }
}
