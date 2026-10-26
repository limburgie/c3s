package be.webfactor.c3s.contentrepository.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import org.apache.commons.lang3.LocaleUtils;

import be.webfactor.c3s.siteassetstore.domain.LocationThreadLocal;

/**
 * Helper class for formatting dates with optional locale or time zone settings.
 */
public class DateBuilder {

	private TemporalAccessor temporalAccessor;
	private String pattern;
	private Locale locale;
	private ZoneId timeZone;

	public DateBuilder(TemporalAccessor temporalAccessor, String pattern) {
		this.temporalAccessor = temporalAccessor;
		this.pattern = pattern;
		this.locale = LocationThreadLocal.getLocaleContext().locale();
		this.timeZone = LocationThreadLocal.getTimeZone();
	}

	/**
	 * Uses the given locale to format this date.
	 */
	public DateBuilder withLocale(String locale) {
		this.locale = LocaleUtils.toLocale(locale);

		return this;
	}

	/**
	 * Uses the given time zone to format this date.
	 */
	public DateBuilder withTimeZone(String timeZone) {
		this.timeZone = ZoneId.of(timeZone);

		return this;
	}

	/**
	 * Returns this date as an instant, or null if there is no date. A date without offset is interpreted in the
	 * configured time zone, and a date without time at the start of that day.
	 */
	public Instant toInstant() {
		if (temporalAccessor == null) {
			return null;
		}
		if (temporalAccessor.isSupported(ChronoField.INSTANT_SECONDS)) {
			return Instant.from(temporalAccessor);
		}
		if (temporalAccessor.isSupported(ChronoField.NANO_OF_DAY)) {
			return LocalDateTime.from(temporalAccessor).atZone(timeZone).toInstant();
		}
		return LocalDate.from(temporalAccessor).atStartOfDay(timeZone).toInstant();
	}

	/**
	 * Outputs this date's string representation, taking into account configured locale and time zone.
	 */
	public String toString() {
		return temporalAccessor == null ? "" : DateTimeFormatter.ofPattern(pattern).withZone(timeZone).withLocale(locale).format(temporalAccessor);
	}
}