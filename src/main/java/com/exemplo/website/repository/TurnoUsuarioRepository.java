package com.exemplo.website.repository;

import com.exemplo.website.model.TurnoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TurnoUsuarioRepository extends JpaRepository<TurnoUsuario, Long> {

    List<TurnoUsuario> findByTurno_Id(Long turnoId);

    List<TurnoUsuario> findByUsuario_Id(Long usuarioId);

    boolean existsByTurno_IdAndUsuario_Id(Long turnoId, Long usuarioId);

}