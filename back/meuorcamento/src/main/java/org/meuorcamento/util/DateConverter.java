package org.meuorcamento.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.TimeZone;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.DateTimeConverter;
import javax.faces.convert.FacesConverter;



@FacesConverter(value="dateConverter")
public class DateConverter implements Converter {

	private DateTimeConverter converter = new DateTimeConverter();

	public DateConverter() {
		converter.setPattern("dd/MM/yyyy");
		converter.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (null == value || value.isEmpty()) {
			return null;
		}

		LocalDate localDate = null;

		try {
			localDate = LocalDate.parse(value.trim(),
					DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		} catch (DateTimeParseException e) {
			throw new ConverterException("O formato da data e hora deve ser 13/11/2015.");
		}
		return localDate;

	}


	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (null == value) {
			return "";
		}

		return ((LocalDate) value).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")
				.withZone(ZoneId.systemDefault()));
	}



}







