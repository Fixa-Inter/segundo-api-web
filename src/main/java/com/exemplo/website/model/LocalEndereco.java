package com.exemplo.website.model;

import com.exemplo.website.model.Enum.TipoLocalEndereco;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "local_unidade")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocalEndereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "tipo_local_unidade", nullable = false)
    private TipoLocalEndereco tipoLocalEndereco;

    @Column(name = "descricao", nullable = false, length = 255)
    private String descricao;

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
