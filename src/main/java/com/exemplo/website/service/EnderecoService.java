package com.exemplo.website.service;

import com.exemplo.website.dto.endereco.EnderecoRequest;
import com.exemplo.website.dto.endereco.EnderecoResponse;
import com.exemplo.website.dto.endereco.EnderecoUpdateRequest;
import com.exemplo.website.model.Endereco;
import com.exemplo.website.model.Instituicao;
import com.exemplo.website.repository.EnderecoRepository;
import com.exemplo.website.repository.InstituicaoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final InstituicaoRepository instituicaoRepository;

    public EnderecoService(
            EnderecoRepository enderecoRepository,
            InstituicaoRepository instituicaoRepository
    ) {
        this.enderecoRepository = enderecoRepository;
        this.instituicaoRepository = instituicaoRepository;
    }

    public EnderecoResponse cadastrar(EnderecoRequest request) {

        validarCadastro(request);

        Instituicao instituicao = instituicaoRepository
                .findById(request.instituicaoId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Instituição não encontrada")
                );

        Endereco endereco = new Endereco();

        endereco.setInstituicao(instituicao);
        endereco.setLogradouro(request.logradouro().trim());
        endereco.setNumero(request.numero().trim());
        endereco.setComplemento(request.complemento());
        endereco.setBairro(request.bairro().trim());
        endereco.setCidade(request.cidade().trim());
        endereco.setEstado(request.estado().trim().toUpperCase());
        endereco.setPais(request.pais());
        endereco.setCep(normalizarCep(request.cep()));
        endereco.setCnpj(normalizarCnpj(request.cnpj()));

        Endereco enderecoSalvo = enderecoRepository.save(endereco);

        return toResponse(enderecoSalvo);
    }

    public List<EnderecoResponse> listarTodos() {
        return enderecoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public EnderecoResponse buscarPorId(Long id) {
        return toResponse(buscarEndereco(id));
    }

    public EnderecoResponse atualizar(
            Long id,
            EnderecoUpdateRequest request
    ) {

        Endereco endereco = buscarEndereco(id);

        if (request.instituicaoId() != null) {
            Instituicao instituicao = instituicaoRepository
                    .findById(request.instituicaoId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Instituição não encontrada")
                    );

            endereco.setInstituicao(instituicao);
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
            if (request.estado().length() != 2) {
                throw new IllegalArgumentException(
                        "Estado deve possuir 2 caracteres"
                );
            }

            endereco.setEstado(
                    request.estado().trim().toUpperCase()
            );
        }

        if (request.pais() != null) {
            endereco.setPais(request.pais());
        }

        if (request.cep() != null) {
            endereco.setCep(
                    normalizarCep(request.cep())
            );
        }

        if (request.cnpj() != null) {
            endereco.setCnpj(
                    normalizarCnpj(request.cnpj())
            );
        }

        Endereco enderecoAtualizado =
                enderecoRepository.save(endereco);

        return toResponse(enderecoAtualizado);
    }

    public EnderecoResponse ativar(Long id) {

        Endereco endereco = buscarEndereco(id);

        endereco.setEstaAtivo(true);

        return toResponse(
                enderecoRepository.save(endereco)
        );
    }

    public EnderecoResponse desativar(Long id) {

        Endereco endereco = buscarEndereco(id);

        endereco.setEstaAtivo(false);

        return toResponse(
                enderecoRepository.save(endereco)
        );
    }

    private Endereco buscarEndereco(Long id) {
        return enderecoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Endereço não encontrado"
                        )
                );
    }

    private void validarCadastro(EnderecoRequest request) {

        if (request.instituicaoId() == null) {
            throw new IllegalArgumentException(
                    "Instituição é obrigatória"
            );
        }

        if (request.logradouro() == null ||
                request.logradouro().isBlank()) {
            throw new IllegalArgumentException(
                    "Logradouro é obrigatório"
            );
        }

        if (request.numero() == null ||
                request.numero().isBlank()) {
            throw new IllegalArgumentException(
                    "Número é obrigatório"
            );
        }

        if (request.bairro() == null ||
                request.bairro().isBlank()) {
            throw new IllegalArgumentException(
                    "Bairro é obrigatório"
            );
        }

        if (request.cidade() == null ||
                request.cidade().isBlank()) {
            throw new IllegalArgumentException(
                    "Cidade é obrigatória"
            );
        }

        if (request.estado() == null ||
                request.estado().length() != 2) {
            throw new IllegalArgumentException(
                    "Estado deve possuir 2 caracteres"
            );
        }

        if (request.cep() == null ||
                normalizarCep(request.cep()).length() != 8) {
            throw new IllegalArgumentException(
                    "CEP deve possuir 8 dígitos"
            );
        }

        if (request.cnpj() == null ||
                normalizarCnpj(request.cnpj()).length() != 14) {
            throw new IllegalArgumentException(
                    "CNPJ deve possuir 14 dígitos"
            );
        }
    }

    private String normalizarCep(String cep) {
        return cep.replaceAll("\\D", "");
    }

    private String normalizarCnpj(String cnpj) {
        return cnpj.replaceAll("\\D", "");
    }

    private EnderecoResponse toResponse(
            Endereco endereco
    ) {

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