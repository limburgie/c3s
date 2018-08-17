package be.webfactor.c3s.content.service.contentful;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;

import be.webfactor.c3s.content.service.domain.DateBuilder;
import be.webfactor.c3s.content.service.domain.DateField;

public class ContentfulDateField implements DateField {

	private static final String DATE_PATTERN = "yyyy-MM-dd";
	private static final String DATETIME_WITHOUT_TIMEZONE_PATTERN = "yyyy-MM-dd'T'HH:mm";
	private static final String DATETIME_WITH_TIMEZONE_PATTERN = "yyyy-MM-dd'T'HH:mmXXX";

	private TemporalAccessor temporalAccessor;

	ContentfulDateField(String stringRepresentationOfDate) {
		DateTimeFormatter dateTimeWithTimezoneFormatter = DateTimeFormatter.ofPattern(DATETIME_WITH_TIMEZONE_PATTERN);
		DateTimeFormatter dateTimeWithoutTimezoneFormatter = DateTimeFormatter.ofPattern(DATETIME_WITHOUT_TIMEZONE_PATTERN);
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);

		try {
			temporalAccessor = LocalDateTime.parse(stringRepresentationOfDate, dateTimeWithTimezoneFormatter);
		} catch (DateTimeParseException e1) {
			try {
				temporalAccessor = LocalDateTime.parse(stringRepresentationOfDate, dateTimeWithoutTimezoneFormatter);
			} catch (DateTimeParseException e2) {
				temporalAccessor = LocalDate.parse(stringRepresentationOfDate, dateFormatter);
			}
		}
	}

	public DateBuilder format(String pattern) {
		return new DateBuilder(temporalAccessor, pattern);
	}
}