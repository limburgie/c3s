package be.webfactor.c3s.content.service.domain;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import org.apache.commons.lang3.LocaleUtils;

import be.webfactor.c3s.master.domain.LocationThreadLocal;

/**
 * Helper class for formatting dates with optional locale or time zone settings.
 */
public class DateBuilder {

	private TemporalAccessor temporalAccessor;
	private String pattern;
	private String locale;
	private String timeZone;

	public DateBuilder(TemporalAccessor temporalAccessor, String pattern) {
		this.temporalAccessor = temporalAccessor;
		this.pattern = pattern;
		this.locale = LocationThreadLocal.getLocale();
		this.timeZone = LocationThreadLocal.getTimeZone();
	}

	/**
	 * Uses the given locale to format this date.
	 */
	public DateBuilder withLocale(String locale) {
		this.locale = locale;

		return this;
	}

	/**
	 * Uses the given time zone to format this date.
	 */
	public DateBuilder withTimeZone(String timeZone) {
		this.timeZone = timeZone;

		return this;
	}

	/**
	 * Outputs this date's string representation, taking into account configured locale and time zone.
	 */
	public String toString() {
		return temporalAccessor == null ? "" : DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of(timeZone)).withLocale(LocaleUtils.toLocale(locale)).format(temporalAccessor);
	}
}