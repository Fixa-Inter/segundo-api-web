package com.exemplo.website.model.Enum;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CategoriaProblema {
    ELETRICA("Elétrica", 1),
    HIDRAULICA("Hidráulica", 2),
    MECANICA("Mecânica", 3),
    INFORMATICA("Informática", 4),
    MOBILIARIO("Mobiliário", 5),
    CLIMATIZACAO("Climatização", 6),
    REDE_CONECTIVIDADE("Rede e Conectividade", 7),
    SEGURANCA("Segurança", 8),
    ILUMINACAO("Iluminação", 9),
    LIMPEZA_CONSERVACAO("Limpeza e Conservação", 10);

    private final String nome;
    private final int id;

    public static CategoriaProblema fromNome(String nome) {
        for (CategoriaProblema valor : values()) {
            if (valor.getNome().equals(nome)) {
                return valor;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + nome);
    }

    public static CategoriaProblema fromId(int id) {
        for (CategoriaProblema valor : values()) {
            if (valor.getId() == id) {
                return valor;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + id);
    }
}
