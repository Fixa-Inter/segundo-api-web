package com.exemplo.website.model.Converter;

import com.exemplo.website.model.Enum.CategoriaProblema;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CategoriaProblemaConverter implements AttributeConverter<CategoriaProblema, Integer> {
    @Override
    public Integer convertToDatabaseColumn(CategoriaProblema attribute) {
        return attribute == null ? null : attribute.getId();
    }

    @Override
    public CategoriaProblema convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : CategoriaProblema.fromId(dbData);
    }
}
