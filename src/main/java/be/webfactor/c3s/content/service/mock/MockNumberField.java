package be.webfactor.c3s.content.service.mock;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.apache.commons.lang3.LocaleUtils;

import be.webfactor.c3s.content.service.domain.NumberField;

public class MockNumberField implements NumberField {

	public String format(String pattern) {
		return format(pattern, "en_US");
	}

	public String format(String pattern, String locale) {
		return new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(LocaleUtils.toLocale(locale))).format(MockRandomGenerator.number());
	}

	public boolean isEmpty() {
		return false;
	}
}
