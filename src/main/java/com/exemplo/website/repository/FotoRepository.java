package com.exemplo.website.repository;

import com.exemplo.website.model.Foto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FotoRepository extends JpaRepository<Foto, Long> {

    Optional<Foto> findFirstByUsuario_IdOrderByIdDesc(Long usuarioId);
}
