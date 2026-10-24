package com.exemplo.website.model.Enum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Persists the database IDs declared by TipoAcesso, not enum ordinals. */
@Converter
public class TipoAcessoConverter implements AttributeConverter<TipoAcesso, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TipoAcesso tipoAcesso) {
        return tipoAcesso == null ? null : tipoAcesso.getId();
    }

    @Override
    public TipoAcesso convertToEntityAttribute(Integer id) {
        return id == null ? null : TipoAcesso.fromId(id);
    }
}
