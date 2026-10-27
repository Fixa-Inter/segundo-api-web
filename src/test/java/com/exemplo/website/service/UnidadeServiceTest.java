package com.exemplo.website.service;

import com.exemplo.website.dto.unidade.UnidadeResponse;
import com.exemplo.website.model.Instituicao;
import com.exemplo.website.model.Unidade;
import com.exemplo.website.repository.InstituicaoRepository;
import com.exemplo.website.repository.UnidadeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UnidadeServiceTest {

    private final UnidadeRepository unidadeRepository = mock(UnidadeRepository.class);
    private final InstituicaoRepository instituicaoRepository = mock(InstituicaoRepository.class);
    private final UnidadeService service = new UnidadeService(
            unidadeRepository,
            instituicaoRepository
    );

    @Test
    void listaUnidadesFiltradasPelaInstituicao() {
        Instituicao instituicao = new Instituicao();
        instituicao.setId(7L);

        Unidade unidade = new Unidade();
        unidade.setId(12L);
        unidade.setInstituicao(instituicao);
        unidade.setNome("Filial Centro");

        when(instituicaoRepository.findById(7L)).thenReturn(Optional.of(instituicao));
        when(unidadeRepository.findByInstituicao_Id(7L)).thenReturn(List.of(unidade));

        List<UnidadeResponse> response = service.listarPorInstituicao(7L);

        assertEquals(1, response.size());
        assertEquals(12L, response.get(0).id());
        assertEquals(7L, response.get(0).instituicaoId());
        assertEquals("Filial Centro", response.get(0).nome());
        verify(unidadeRepository).findByInstituicao_Id(7L);
        verify(unidadeRepository, never()).findAll();
    }

    @Test
    void rejeitaFiltroParaInstituicaoInexistente() {
        when(instituicaoRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.listarPorInstituicao(404L));
        verify(unidadeRepository, never()).findByInstituicao_Id(404L);
    }
}
