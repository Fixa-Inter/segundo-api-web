package com.exemplo.website.service;

import com.exemplo.website.dto.endereco.EnderecoRequest;
import com.exemplo.website.dto.endereco.EnderecoResponse;
import com.exemplo.website.dto.endereco.EnderecoUpdateRequest;
import com.exemplo.website.model.Endereco;
import com.exemplo.website.model.Instituicao;
import com.exemplo.website.repository.EnderecoRepository;
import com.exemplo.website.repository.InstituicaoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final InstituicaoRepository instituicaoRepository;

    public EnderecoResponse cadastrar(EnderecoRequest request) {

        validarCnpjNovo(request.cnpj());

        Instituicao instituicao = buscarInstituicao(request.instituicaoId());

        Endereco endereco = new Endereco();

        endereco.setInstituicao(instituicao);
        endereco.setLogradouro(request.logradouro().trim());
        endereco.setNumero(request.numero().trim());
        endereco.setComplemento(request.complemento());
        endereco.setBairro(request.bairro().trim());
        endereco.setCidade(request.cidade().trim());
        endereco.setEstado(request.estado().trim().toUpperCase());
        endereco.setPais(
                request.pais() == null
                        ? null
                        : request.pais().trim()
        );
        endereco.setCep(validarCep(request.cep()));
        endereco.setCnpj(normalizarNumero(request.cnpj()));

        return toResponse(enderecoRepository.save(endereco));
    }

    public List<EnderecoResponse> listarTodos() {
        return enderecoRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public EnderecoResponse buscarPorId(Long id) {
        return toResponse(buscarEntidade(id));
    }

    public EnderecoResponse atualizar(
            Long id,
            EnderecoUpdateRequest request
    ) {

        Endereco endereco = buscarEntidade(id);

        if (request.instituicaoId() != null) {
            endereco.setInstituicao(
                    buscarInstituicao(request.instituicaoId())
            );
        }

        if (request.logradouro() != null) {
            endereco.setLogradouro(request.logradouro().trim());
        }

        if (request.numero() != null) {
            endereco.setNumero(request.numero().trim());
        }

        if (request.complemento() != null) {
            endereco.setComplemento(request.complemento());
        }

        if (request.bairro() != null) {
            endereco.setBairro(request.bairro().trim());
        }

        if (request.cidade() != null) {
            endereco.setCidade(request.cidade().trim());
        }

        if (request.estado() != null) {

            if (request.estado().trim().length() != 2) {
                throw new IllegalArgumentException(
                        "O estado deve possuir 2 caracteres"
                );
            }

            endereco.setEstado(
                    request.estado().trim().toUpperCase()
            );
        }

        if (request.pais() != null) {
            endereco.setPais(request.pais().trim());
        }

        if (request.cep() != null) {

            endereco.setCep(validarCep(request.cep()));
        }

        if (request.cnpj() != null) {

            String cnpj = normalizarNumero(request.cnpj());

            if (cnpj.length() != 14) {
                throw new IllegalArgumentException(
                        "O CNPJ deve possuir 14 dígitos"
                );
            }

            if (enderecoRepository.existsByCnpjAndIdNot(cnpj, id)) {
                throw new IllegalArgumentException(
                        "Já existe um endereço cadastrado com esse CNPJ"
                );
            }

            endereco.setCnpj(cnpj);
        }

        return toResponse(enderecoRepository.save(endereco));
    }

    public EnderecoResponse ativar(Long id) {

        Endereco endereco = buscarEntidade(id);

        endereco.setEstaAtivo(true);

        return toResponse(enderecoRepository.save(endereco));
    }

    public EnderecoResponse desativar(Long id) {

        Endereco endereco = buscarEntidade(id);

        endereco.setEstaAtivo(false);

        return toResponse(enderecoRepository.save(endereco));
    }

    private Endereco buscarEntidade(Long id) {
        return enderecoRepository
                .findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Endereço não encontrado"
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

        if (enderecoRepository.existsByCnpj(cnpj)) {
            throw new IllegalArgumentException(
                    "Já existe um endereço cadastrado com esse CNPJ"
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

    private EnderecoResponse toResponse(Endereco endereco) {

        return new EnderecoResponse(
                endereco.getId(),
                endereco.getInstituicao().getId(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado(),
                endereco.getPais(),
                endereco.getCep(),
                endereco.getCnpj(),
                endereco.getDataCriacao(),
                endereco.getEstaAtivo()
        );
    }
}
