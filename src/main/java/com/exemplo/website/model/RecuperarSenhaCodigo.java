package com.exemplo.website.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecuperarSenhaCodigo {

    private Long usuarioId;

    private Integer codigo;
}