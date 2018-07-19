package be.webfactor.c3s.content.service.domain;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import org.apache.commons.lang3.LocaleUtils;

public class DateBuilder {

	private TemporalAccessor temporalAccessor;
	private String pattern;
	private String locale = Locale.getDefault().toString();
	private String timeZone = ZoneId.systemDefault().getId();

	public DateBuilder(TemporalAccessor temporalAccessor, String pattern) {
		this.temporalAccessor = temporalAccessor;
		this.pattern = pattern;
	}

	public DateBuilder withLocale(String locale) {
		this.locale = locale;

		return this;
	}

	public DateBuilder withTimeZone(String timeZone) {
		this.timeZone = timeZone;

		return this;
	}

	public String toString() {
		return DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of(timeZone)).withLocale(LocaleUtils.toLocale(locale)).format(temporalAccessor);
	}
}