package be.webfactor.c3s.contentrepository.contentful;

import be.webfactor.c3s.contentrepository.domain.NumberBuilder;
import be.webfactor.c3s.contentrepository.domain.NumberField;

public class ContentfulNumberField implements NumberField {

	private Double value;

	ContentfulNumberField(Double value) {
		this.value = value;
	}

	public NumberBuilder format(String pattern) {
		return new NumberBuilder(value, pattern);
	}
}