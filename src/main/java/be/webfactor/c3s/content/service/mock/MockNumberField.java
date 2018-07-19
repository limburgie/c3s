package be.webfactor.c3s.content.service.mock;

import be.webfactor.c3s.content.service.domain.NumberBuilder;
import be.webfactor.c3s.content.service.domain.NumberField;

public class MockNumberField implements NumberField {

	public NumberBuilder format(String pattern) {
		return new NumberBuilder(MockRandomGenerator.number(), pattern);
	}

	public boolean isEmpty() {
		return false;
	}
}
