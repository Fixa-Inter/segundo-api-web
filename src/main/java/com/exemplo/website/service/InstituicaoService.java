package com.exemplo.website.service;

import com.exemplo.website.dto.instituicao.InstituicaoRequest;
import com.exemplo.website.dto.instituicao.InstituicaoResponse;
import com.exemplo.website.dto.instituicao.InstituicaoUpdateRequest;
import com.exemplo.website.model.Instituicao;
import com.exemplo.website.repository.InstituicaoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstituicaoService {

    private final InstituicaoRepository instituicaoRepository;

    public InstituicaoResponse cadastrar(
            InstituicaoRequest request
    ) {

        String dominio =
                normalizarDominio(request.dominioEmail());

        if (
                instituicaoRepository
                        .existsByDominioEmail(dominio)
        ) {
            throw new IllegalArgumentException(
                    "Já existe uma instituição com esse domínio"
            );
        }

        Instituicao instituicao = new Instituicao();

        instituicao.setNome(request.nome().trim());
        instituicao.setTipoInstituicao(
                request.tipoInstituicao()
        );
        instituicao.setDominioEmail(dominio);

        return toResponse(
                instituicaoRepository.save(instituicao)
        );
    }

    public List<InstituicaoResponse> listarTodos() {

        return instituicaoRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public InstituicaoResponse buscarPorId(Long id) {
        return toResponse(buscarEntidade(id));
    }

    public InstituicaoResponse atualizar(
            Long id,
            InstituicaoUpdateRequest request
    ) {

        Instituicao instituicao = buscarEntidade(id);

        if (request.nome() != null) {
            instituicao.setNome(
                    request.nome().trim()
            );
        }

        if (request.tipoInstituicao() != null) {
            instituicao.setTipoInstituicao(
                    request.tipoInstituicao()
            );
        }

        if (request.dominioEmail() != null) {

            String dominio =
                    normalizarDominio(
                            request.dominioEmail()
                    );

            if (
                    instituicaoRepository
                            .existsByDominioEmailAndIdNot(
                                    dominio,
                                    id
                            )
            ) {
                throw new IllegalArgumentException(
                        "Já existe uma instituição com esse domínio"
                );
            }

            instituicao.setDominioEmail(dominio);
        }

        return toResponse(
                instituicaoRepository.save(instituicao)
        );
    }

    public InstituicaoResponse ativar(Long id) {

        Instituicao instituicao =
                buscarEntidade(id);

        instituicao.setEstaAtivo(true);

        return toResponse(
                instituicaoRepository.save(instituicao)
        );
    }

    public InstituicaoResponse desativar(Long id) {

        Instituicao instituicao =
                buscarEntidade(id);

        instituicao.setEstaAtivo(false);

        return toResponse(
                instituicaoRepository.save(instituicao)
        );
    }

    private Instituicao buscarEntidade(Long id) {

        return instituicaoRepository
                .findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Instituição não encontrada"
                        )
                );
    }

    private String normalizarDominio(String dominio) {

        String resultado =
                dominio.trim().toLowerCase();

        if (resultado.startsWith("@")) {
            resultado = resultado.substring(1);
        }

        return resultado;
    }

    private InstituicaoResponse toResponse(
            Instituicao instituicao
    ) {

        return new InstituicaoResponse(
                instituicao.getId(),
                instituicao.getNome(),
                instituicao.getTipoInstituicao(),
                instituicao.getDominioEmail(),
                instituicao.getDataCriacao(),
                instituicao.getEstaAtivo()
        );
    }
}