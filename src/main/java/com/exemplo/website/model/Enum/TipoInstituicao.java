package com.exemplo.website.model.Enum;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TipoInstituicao {
    ESCOLA("Escola", 1),
    FACULDADE("Faculdade", 2),
    EMPRESA("Empresa", 3);

    private final String nome;
    private final int id;

    public static TipoInstituicao fromNome(String nome) {
        for (TipoInstituicao valor : values()) {
            if (valor.getNome().equals(nome)) {
                return valor;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + nome);
    }

    public static TipoInstituicao fromId(int id) {
        for (TipoInstituicao valor : values()) {
            if (valor.getId() == id) {
                return valor;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + id);
    }
}
