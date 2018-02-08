package be.webfactor.c3s.content.service.prismic;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import org.apache.commons.lang3.LocaleUtils;

import be.webfactor.c3s.content.service.domain.DateField;
import io.prismic.Fragment;

public class PrismicDateField implements DateField {

	private Fragment fragment;

	PrismicDateField(Fragment fragment) {
		this.fragment = fragment;
	}

	public String format(String pattern) {
		return format(pattern, Locale.getDefault().toString());
	}

	public String format(String pattern, String locale) {
		if (fragment == null) {
			return "";
		}

		if (fragment instanceof Fragment.Date) {
			return format(((Fragment.Date) fragment).getValue(), pattern, locale);
		} else {
			return format(((Fragment.Timestamp) fragment).getValue(), pattern, locale);
		}
	}

	private String format(TemporalAccessor javaDate, String pattern, String locale) {
		return DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault()).withLocale(LocaleUtils.toLocale(locale)).format(javaDate);
	}

	public boolean isEmpty() {
		return fragment == null;
	}
}