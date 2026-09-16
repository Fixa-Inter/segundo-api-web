package com.exemplo.website.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "turno_usuario", uniqueConstraints = @UniqueConstraint(
        name = "uk_turno_usuario", columnNames = {"turno_id", "usuario_id"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TurnoUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "turno_id", nullable = false)
    private Turno turno;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

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
