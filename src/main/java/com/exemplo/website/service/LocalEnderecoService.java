package com.exemplo.website.service;

import com.exemplo.website.dto.localendereco.LocalEnderecoRequest;
import com.exemplo.website.dto.localendereco.LocalEnderecoResponse;
import com.exemplo.website.dto.localendereco.LocalEnderecoUpdateRequest;
import com.exemplo.website.model.Endereco;
import com.exemplo.website.model.LocalEndereco;
import com.exemplo.website.repository.EnderecoRepository;
import com.exemplo.website.repository.LocalEnderecoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocalEnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final LocalEnderecoRepository localEnderecoRepository;

    public LocalEnderecoResponse cadastrar(
            LocalEnderecoRequest request
    ) {

        Endereco endereco =
                enderecoRepository
                        .findById(request.enderecoId())
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Endereço não encontrado"
                                )
                        );

        LocalEndereco local = new LocalEndereco();

        local.setEndereco(endereco);
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

    public List<LocalEnderecoResponse> listarTodos() {

        return localEnderecoRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<LocalEnderecoResponse> listarPorEndereco(
            Long enderecoId
    ) {

        return localEnderecoRepository
                .findByEndereco_Id(enderecoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public LocalEnderecoResponse buscarPorId(Long id) {
        return toResponse(buscarEntidade(id));
    }

    public LocalEnderecoResponse atualizar(
            Long id,
            LocalEnderecoUpdateRequest request
    ) {

        LocalEndereco local =
                buscarEntidade(id);

        if (request.enderecoId() != null) {

            Endereco endereco =
                    enderecoRepository
                            .findById(request.enderecoId())
                            .orElseThrow(() ->
                                    new EntityNotFoundException(
                                            "Endereço não encontrado"
                                    )
                            );

            local.setEndereco(endereco);
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

    public LocalEnderecoResponse ativar(Long id) {

        LocalEndereco local =
                buscarEntidade(id);

        local.setEstaAtivo(true);

        return toResponse(
                localEnderecoRepository.save(local)
        );
    }

    public LocalEnderecoResponse desativar(Long id) {

        LocalEndereco local =
                buscarEntidade(id);

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

    private LocalEnderecoResponse toResponse(
            LocalEndereco local
    ) {

        return new LocalEnderecoResponse(
                local.getId(),
                local.getEndereco().getId(),
                local.getNome(),
                local.getTipoLocalEndereco(),
                local.getDescricao(),
                local.getDataCriacao(),
                local.getEstaAtivo()
        );
    }
}