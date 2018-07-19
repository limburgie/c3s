package be.webfactor.c3s.content.service.domain;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.commons.lang3.LocaleUtils;

/**
 * Helper class for formatting numbers with optional locale setting.
 */
public class NumberBuilder {

	private Double number;
	private String pattern;
	private String locale = Locale.getDefault().toString();

	public NumberBuilder(Double number, String pattern) {
		this.number = number;
		this.pattern = pattern;
	}

	/**
	 * Uses the given locale to format this number.
	 */
	public NumberBuilder withLocale(String locale) {
		this.locale = locale;

		return this;
	}

	/**
	 * Outputs this number's string representation, taking into account configured locale.
	 */
	public String toString() {
		return number == null ? "" : new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(LocaleUtils.toLocale(locale))).format(number);
	}
}