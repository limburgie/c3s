package be.webfactor.c3s.content.service.prismic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import org.apache.commons.lang3.LocaleUtils;

import be.webfactor.c3s.content.service.domain.DateField;
import io.prismic.Fragment;

public class PrismicDateField implements DateField {

	private Fragment fragment;

	PrismicDateField(Fragment fragment) {
		this.fragment = fragment;
	}

	public String format(String pattern) {
		if (fragment == null) {
			return "";
		}

		if (fragment instanceof Fragment.Date) {
			return ((Fragment.Date) fragment).asText(pattern);
		} else {
			return ((Fragment.Timestamp) fragment).asText(pattern);
		}
	}

	public String format(String pattern, String locale) {
		if (fragment == null) {
			return "";
		}

		if (fragment instanceof Fragment.Date) {
			org.joda.time.LocalDate jodaDate = ((Fragment.Date) fragment).getValue();
			LocalDate javaDate = LocalDate.of(jodaDate.getYear(), jodaDate.getMonthOfYear(), jodaDate.getDayOfMonth());
			return format(javaDate, pattern, locale);
		} else {
			org.joda.time.DateTime jodaDateTime = ((Fragment.Timestamp) fragment).getValue();
			LocalDateTime javaDateTime = LocalDateTime.of(jodaDateTime.getYear(), jodaDateTime.getMonthOfYear(), jodaDateTime.getDayOfMonth(), jodaDateTime.getHourOfDay(), jodaDateTime.getMinuteOfHour());
			return format(javaDateTime, pattern, locale);
		}
	}

	private String format(TemporalAccessor javaDate, String pattern, String locale) {
		return DateTimeFormatter.ofPattern(pattern).withLocale(LocaleUtils.toLocale(locale)).format(javaDate);
	}
}