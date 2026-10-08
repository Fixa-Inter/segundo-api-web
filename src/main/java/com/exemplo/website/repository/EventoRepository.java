package com.exemplo.website.repository;

import com.exemplo.website.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByUsuario_Id(Long usuarioId);

}