package com.exemplo.website.model.Enum;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TipoAcesso {
    ADMINISTRADOR("Administrador", 1),
    GESTOR("Gestor", 2),
    TECNICO("Técnico", 3),
    SOLICITANTE("Solicitante", 4);

    private final String nome;
    private final int id;

    public static TipoAcesso fromNome(String nome) {
        for (TipoAcesso valor : values()) {
            if (valor.getNome().equals(nome)) {
                return valor;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + nome);
    }

    public static TipoAcesso fromId(int id) {
        for (TipoAcesso valor : values()) {
            if (valor.getId() == id) {
                return valor;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + id);
    }
}
