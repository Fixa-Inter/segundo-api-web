package com.exemplo.website.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "hora_inicio", nullable = false)
    private Integer horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private Integer horaFim;

    @Column(name = "atravessa_meia_noite", nullable = false)
    private Boolean atravessaMeiaNoite = false;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "esta_ativo", nullable = false)
    private Boolean estaAtivo = true;


    @PrePersist
    private void preencherDataCriacao() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }
}
