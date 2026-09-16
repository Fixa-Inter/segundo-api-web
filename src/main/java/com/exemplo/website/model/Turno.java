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
    @JoinColumn(name = "endereco_id", nullable = false)
    private Endereco endereco;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "hora_inicio", nullable = false)
    private LocalDateTime horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private LocalDateTime horaFim;

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