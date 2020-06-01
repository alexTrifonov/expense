package com.expense.serializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Класс для сериализации объекта типа java.time.LocalDate в строковое значение в формате ISO.
 * @author Alexandr Trifonov
 *
 */
public class LocalDateSerializer extends StdSerializer<LocalDate> {
	public LocalDateSerializer() {
		super(LocalDate.class);
	}

	@Override
	public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE));
		
	}
}
