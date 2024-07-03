package fr.insee.formation.hibernate.model.converter;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class YearMonthConverter implements AttributeConverter<YearMonth, Date> {

	@Override
	public Date convertToDatabaseColumn(YearMonth entityValue) {
		return Date.from(entityValue.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	@Override
	public YearMonth convertToEntityAttribute(Date dbValue) {
		return YearMonth.from(dbValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
	}

}
