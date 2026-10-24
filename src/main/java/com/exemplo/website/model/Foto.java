package com.exemplo.website.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = true)
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuario;

    @Column(name = "url", nullable = false)
    private String url;

}
