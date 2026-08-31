package com.exemplo.website.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Nota {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "nota")
    private Double nota;

    @Column(name = "data_criacao")
    private LocalDate dataCriacao;

    @Column(name = "esta_ativo")
    private Boolean estaAtivo;


}
