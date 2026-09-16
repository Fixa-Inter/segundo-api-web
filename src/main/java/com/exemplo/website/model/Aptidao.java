package com.exemplo.website.model;

import com.exemplo.website.model.Enum.CategoriaProblema;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "aptidao", uniqueConstraints = @UniqueConstraint(
        name = "uk_usuario_categoria_aptidao", columnNames = {"usuario_id", "categoria_problema"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Aptidao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "categoria_problema", nullable = false)
    private CategoriaProblema categoriaProblema;

    @DecimalMin("0.00")
    @DecimalMax("10.00")
    @Column(name = "nota", nullable = false)
    private BigDecimal nota;

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