package com.exemplo.website.service;

import com.exemplo.website.dto.unidade.UnidadeRequest;
import com.exemplo.website.dto.unidade.UnidadeResponse;
import com.exemplo.website.dto.unidade.UnidadeUpdateRequest;
import com.exemplo.website.model.Unidade;
import com.exemplo.website.model.Instituicao;
import com.exemplo.website.repository.UnidadeRepository;
import com.exemplo.website.repository.InstituicaoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;
    private final InstituicaoRepository instituicaoRepository;

    public UnidadeResponse cadastrar(UnidadeRequest request) {

        validarCnpjNovo(request.cnpj());

        Instituicao instituicao = buscarInstituicao(request.instituicaoId());

        Unidade unidade = new Unidade();

        unidade.setInstituicao(instituicao);
        unidade.setNome(request.nome().trim());
        unidade.setLogradouro(request.logradouro().trim());
        unidade.setNumero(request.numero().trim());
        unidade.setComplemento(request.complemento());
        unidade.setBairro(request.bairro().trim());
        unidade.setCidade(request.cidade().trim());
        unidade.setEstado(request.estado().trim().toUpperCase());
        unidade.setCep(validarCep(request.cep()));
        unidade.setCnpj(normalizarNumero(request.cnpj()));

        return toResponse(unidadeRepository.save(unidade));
    }

    public List<UnidadeResponse> listarTodos() {
        return unidadeRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<UnidadeResponse> listarPorInstituicao(Long instituicaoId) {
        buscarInstituicao(instituicaoId);
        return unidadeRepository
                .findByInstituicao_Id(instituicaoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UnidadeResponse buscarPorId(Long id) {
        return toResponse(buscarEntidade(id));
    }

    public UnidadeResponse atualizar(
            Long id,
            UnidadeUpdateRequest request
    ) {

        Unidade unidade = buscarEntidade(id);

        if (request.instituicaoId() != null) {
            unidade.setInstituicao(
                    buscarInstituicao(request.instituicaoId())
            );
        }

        if (request.nome() != null) {
            unidade.setNome(request.nome().trim());
        }

        if (request.logradouro() != null) {
            unidade.setLogradouro(request.logradouro().trim());
        }

        if (request.numero() != null) {
            unidade.setNumero(request.numero().trim());
        }

        if (request.complemento() != null) {
            unidade.setComplemento(request.complemento());
        }

        if (request.bairro() != null) {
            unidade.setBairro(request.bairro().trim());
        }

        if (request.cidade() != null) {
            unidade.setCidade(request.cidade().trim());
        }

        if (request.estado() != null) {

            if (request.estado().trim().length() != 2) {
                throw new IllegalArgumentException(
                        "O estado deve possuir 2 caracteres"
                );
            }

            unidade.setEstado(
                    request.estado().trim().toUpperCase()
            );
        }

        if (request.cep() != null) {

            unidade.setCep(validarCep(request.cep()));
        }

        if (request.cnpj() != null) {

            String cnpj = normalizarNumero(request.cnpj());

            if (cnpj.length() != 14) {
                throw new IllegalArgumentException(
                        "O CNPJ deve possuir 14 dígitos"
                );
            }

            if (unidadeRepository.existsByCnpjAndIdNot(cnpj, id)) {
                throw new IllegalArgumentException(
                        "Já existe uma unidade cadastrada com esse CNPJ"
                );
            }

            unidade.setCnpj(cnpj);
        }

        return toResponse(unidadeRepository.save(unidade));
    }

    public UnidadeResponse ativar(Long id) {

        Unidade unidade = buscarEntidade(id);

        unidade.setEstaAtivo(true);

        return toResponse(unidadeRepository.save(unidade));
    }

    public UnidadeResponse desativar(Long id) {

        Unidade unidade = buscarEntidade(id);

        unidade.setEstaAtivo(false);

        return toResponse(unidadeRepository.save(unidade));
    }

    private Unidade buscarEntidade(Long id) {
        return unidadeRepository
                .findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Unidade não encontrada"
                        )
                );
    }

    private Instituicao buscarInstituicao(Long id) {
        return instituicaoRepository
                .findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Instituição não encontrada"
                        )
                );
    }

    private void validarCnpjNovo(String valor) {

        String cnpj = normalizarNumero(valor);

        if (cnpj.length() != 14) {
            throw new IllegalArgumentException(
                    "O CNPJ deve possuir 14 dígitos"
            );
        }

        if (unidadeRepository.existsByCnpj(cnpj)) {
            throw new IllegalArgumentException(
                    "Já existe uma unidade cadastrada com esse CNPJ"
            );
        }
    }

    private String validarCep(String valor) {
        String cep = normalizarNumero(valor);

        if (cep.length() != 8) {
            throw new IllegalArgumentException(
                    "O CEP deve possuir 8 dígitos"
            );
        }

        return cep;
    }

    private String normalizarNumero(String valor) {
        return valor.replaceAll("\\D", "");
    }

    private UnidadeResponse toResponse(Unidade unidade) {

        return new UnidadeResponse(
                unidade.getId(),
                unidade.getInstituicao().getId(),
                unidade.getNome(),
                unidade.getLogradouro(),
                unidade.getNumero(),
                unidade.getComplemento(),
                unidade.getBairro(),
                unidade.getCidade(),
                unidade.getEstado(),
                unidade.getCep(),
                unidade.getCnpj(),
                unidade.getDataCriacao(),
                unidade.getEstaAtivo()
        );
    }
}
