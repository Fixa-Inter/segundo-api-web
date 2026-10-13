package com.exemplo.website.service;

import com.exemplo.website.dto.aptidao.AptidaoResponse;
import com.exemplo.website.dto.aptidao.AptidaoUpdateRequest;
import com.exemplo.website.model.Aptidao;
import com.exemplo.website.repository.AptidaoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AptidaoService {

    private final AptidaoRepository aptidaoRepository;

    public List<AptidaoResponse> listarPorUsuario(Long usuarioId) {

        return aptidaoRepository
                .findByUsuario_Id(usuarioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AptidaoResponse buscarPorId(Long id) {

        return toResponse(buscarEntidade(id));
    }

    public AptidaoResponse atualizar(
            Long id,
            AptidaoUpdateRequest request
    ) {

        Aptidao aptidao = buscarEntidade(id);

        if (request.nota() != null) {
            validarNota(request.nota());

            aptidao.setNota(request.nota());
        }

        if (request.estaAtivo() != null) {
            aptidao.setEstaAtivo(request.estaAtivo());
        }

        return toResponse(
                aptidaoRepository.save(aptidao)
        );
    }

    private Aptidao buscarEntidade(Long id) {

        return aptidaoRepository
                .findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Aptidão não encontrada"
                        )
                );
    }

    private void validarNota(Double nota) {

        if (!Double.isFinite(nota) || nota < 0 || nota > 10) {
            throw new IllegalArgumentException(
                    "A nota da aptidão deve estar entre 0 e 10"
            );
        }
    }

    private AptidaoResponse toResponse(
            Aptidao aptidao
    ) {

        return new AptidaoResponse(
                aptidao.getId(),
                aptidao.getUsuario().getId(),
                aptidao.getCategoriaProblema(),
                aptidao.getNota(),
                aptidao.getDataCriacao(),
                aptidao.getEstaAtivo()
        );
    }
}
