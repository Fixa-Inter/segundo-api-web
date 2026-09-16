package com.exemplo.website.model;

import com.exemplo.website.model.Enum.TipoInstituicao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(name = "nome", nullable = false)
    private String nome;


    @Column(name = "tipo_instituicao", nullable = false)
    private TipoInstituicao tipoInstituicao;

    @Column(name = "dominio_email", nullable = false, unique = true)
    private String dominioEmail;

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