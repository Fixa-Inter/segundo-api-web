package com.exemplo.website.model.Enum;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TipoLocalEndereco {
    SALA_AULA("Sala de Aula", 1),
    LABORATORIO("Laboratório", 2),
    DEPTO_ADMINISTRATIVO("Depto. Administrativo", 3),
    AUDITORIO("Auditório", 4),
    ALMOXAFIRADO("Almoxarifado", 5),
    AREA_COMUM("Área Comum", 6);

    private final String nome;
    private final int id;

    public static TipoLocalEndereco fromNome(String nome) {
        for (TipoLocalEndereco valor : values()) {
            if (valor.getNome().equals(nome)) {
                return valor;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + nome);
    }

    public static TipoLocalEndereco fromId(int id) {
        for (TipoLocalEndereco valor : values()) {
            if (valor.getId() == id) {
                return valor;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + id);
    }
}
