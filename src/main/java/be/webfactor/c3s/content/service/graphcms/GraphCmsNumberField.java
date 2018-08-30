package be.webfactor.c3s.content.service.graphcms;

import be.webfactor.c3s.content.service.domain.NumberBuilder;
import be.webfactor.c3s.content.service.domain.NumberField;

public class GraphCmsNumberField implements NumberField {

	private Number number;

	GraphCmsNumberField(Number number) {
		this.number = number;
	}

	public NumberBuilder format(String pattern) {
		return new NumberBuilder(number.doubleValue(), pattern);
	}
}