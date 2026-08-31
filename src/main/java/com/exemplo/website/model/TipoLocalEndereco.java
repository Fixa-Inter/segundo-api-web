package com.exemplo.website.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class TipoLocalEndereco {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @OneToMany
    @JoinColumn(name = "local_endereco_id")
    private LocalEndereco localEndereco;

    @Column(name = "nome")
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "data_cricao")
    private LocalDate dataCricao;

    @Column(name = "esta_ativo")
    private Boolean estaAtivo;

}
