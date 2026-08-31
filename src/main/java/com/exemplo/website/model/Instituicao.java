package com.exemplo.website.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Instituicao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "cnpj")
    private String cnpj;

    @Column(name = "email")
    private String email;

    @Column(name = "tipo_instituicao")
    private TipoInstituicao tipoInstituicao;

    @Column(name = "data_cricao")
    private LocalDate dataCricao;

    @Column(name = "esta_ativo")
    private Boolean estaAtivo;

}
