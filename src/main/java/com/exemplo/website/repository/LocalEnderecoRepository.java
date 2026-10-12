package com.exemplo.website.repository;

import com.exemplo.website.model.LocalEndereco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocalEnderecoRepository extends JpaRepository<LocalEndereco, Long> {

    List<LocalEndereco> findByEndereco_Id(Long enderecoId);

}