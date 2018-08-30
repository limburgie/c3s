package be.webfactor.c3s.content.service.prismic;

import be.webfactor.c3s.content.service.domain.NumberBuilder;
import be.webfactor.c3s.content.service.domain.NumberField;
import io.prismic.Fragment;

public class PrismicNumberField implements NumberField {

	private Double value;

	PrismicNumberField(Fragment.Number number) {
		value = number.getValue();
	}

	public NumberBuilder format(String pattern) {
		return new NumberBuilder(value, pattern);
	}
}