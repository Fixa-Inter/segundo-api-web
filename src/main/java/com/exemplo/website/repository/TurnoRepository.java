package com.exemplo.website.repository;

import com.exemplo.website.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    List<Turno> findByEndereco_Id(Long enderecoId);

}