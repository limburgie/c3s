package be.webfactor.c3s.content.service.contentful;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.apache.commons.lang3.LocaleUtils;

import be.webfactor.c3s.content.service.domain.NumberField;

public class ContentfulNumberField implements NumberField {

	private Double value;

	ContentfulNumberField(Double value) {
		this.value = value;
	}

	public String format(String pattern) {
		return format(new DecimalFormat(pattern));
	}

	public String format(String pattern, String locale) {
		return format(new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(LocaleUtils.toLocale(locale))));
	}

	private String format(DecimalFormat df) {
		return value == null ? "" : df.format(value);
	}
}
