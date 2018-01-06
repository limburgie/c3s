package be.webfactor.c3s.content.service.contentful;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;

import org.apache.commons.lang3.LocaleUtils;

import be.webfactor.c3s.content.service.domain.DateField;

public class ContentfulDateField implements DateField {

	private static final String DATE_PATTERN = "yyyy-MM-dd";
	private static final String DATETIME_WITHOUT_TIMEZONE_PATTERN = "yyyy-MM-dd'T'HH:mm";
	private static final String DATETIME_WITH_TIMEZONE_PATTERN = "yyyy-MM-dd'T'HH:mmXXX";

	private TemporalAccessor temporalAccessor;

	ContentfulDateField(String stringRepresentationOfDate) {
		if (stringRepresentationOfDate != null) {
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
	}

	public String format(String pattern) {
		return temporalAccessor == null ? "" : DateTimeFormatter.ofPattern(pattern).format(temporalAccessor);
	}

	public String format(String pattern, String locale) {
		return temporalAccessor == null ? "" : DateTimeFormatter.ofPattern(pattern, LocaleUtils.toLocale(locale)).format(temporalAccessor);
	}
}
