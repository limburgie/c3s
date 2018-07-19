package be.webfactor.c3s.content.service.contentful;

import be.webfactor.c3s.content.service.domain.NumberBuilder;
import be.webfactor.c3s.content.service.domain.NumberField;

public class ContentfulNumberField implements NumberField {

	private Double value;

	ContentfulNumberField(Double value) {
		this.value = value;
	}

	public NumberBuilder format(String pattern) {
		return new NumberBuilder(value, pattern);
	}

	public boolean isEmpty() {
		return value == null;
	}
}