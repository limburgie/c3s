package be.webfactor.c3s.content.service.prismic;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.apache.commons.lang3.LocaleUtils;

import be.webfactor.c3s.content.service.domain.NumberField;
import io.prismic.Fragment;

public class PrismicNumberField implements NumberField {

	private Fragment.Number number;

	PrismicNumberField(Fragment.Number number) {
		this.number = number;
	}

	public String format(String pattern) {
		return format(new DecimalFormat(pattern));
	}

	public String format(String pattern, String locale) {
		return format(new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(LocaleUtils.toLocale(locale))));
	}

	private String format(DecimalFormat df) {
		return number == null ? "" : df.format(number.getValue());
	}
}
