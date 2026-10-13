package com.exemplo.website.repository;

import com.exemplo.website.model.Aptidao;
import com.exemplo.website.model.Enum.CategoriaProblema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AptidaoRepository extends JpaRepository<Aptidao, Long> {

    List<Aptidao> findByUsuario_Id(Long usuarioId);

    boolean existsByUsuario_IdAndCategoriaProblema(
            Long usuarioId,
            CategoriaProblema categoriaProblema
    );

}
