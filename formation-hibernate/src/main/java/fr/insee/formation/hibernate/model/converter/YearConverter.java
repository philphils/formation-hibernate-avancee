package fr.insee.formation.hibernate.model.converter;

import java.time.Year;
import java.time.ZoneId;
import java.util.Date;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class YearConverter implements AttributeConverter<Year, Date> {

	@Override
	public Date convertToDatabaseColumn(Year attribute) {
		return Date.from(attribute.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	@Override
	public Year convertToEntityAttribute(Date dbData) {
		return Year.from(dbData.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
	}

}
