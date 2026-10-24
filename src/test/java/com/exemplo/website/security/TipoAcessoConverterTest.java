package com.exemplo.website.security;

import com.exemplo.website.model.Enum.TipoAcesso;
import com.exemplo.website.model.Enum.TipoAcessoConverter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TipoAcessoConverterTest {

    private final TipoAcessoConverter converter = new TipoAcessoConverter();

    @Test
    void mapsDatabaseIdsToExpectedProfiles() {
        assertEquals(TipoAcesso.ADMINISTRADOR, converter.convertToEntityAttribute(1));
        assertEquals(TipoAcesso.GESTOR, converter.convertToEntityAttribute(2));
        assertEquals(TipoAcesso.TECNICO, converter.convertToEntityAttribute(3));
        assertEquals(TipoAcesso.SOLICITANTE, converter.convertToEntityAttribute(4));
    }

    @Test
    void persistsTheDeclaredIds() {
        assertEquals(1, converter.convertToDatabaseColumn(TipoAcesso.ADMINISTRADOR));
        assertEquals(2, converter.convertToDatabaseColumn(TipoAcesso.GESTOR));
        assertEquals(3, converter.convertToDatabaseColumn(TipoAcesso.TECNICO));
        assertEquals(4, converter.convertToDatabaseColumn(TipoAcesso.SOLICITANTE));
    }

    @Test
    void preservesNullValues() {
        assertNull(converter.convertToDatabaseColumn(null));
        assertNull(converter.convertToEntityAttribute(null));
    }
}
