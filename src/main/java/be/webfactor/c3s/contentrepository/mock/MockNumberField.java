package be.webfactor.c3s.contentrepository.mock;

import be.webfactor.c3s.contentrepository.domain.NumberBuilder;
import be.webfactor.c3s.contentrepository.domain.NumberField;

public class MockNumberField implements NumberField {

	public NumberBuilder format(String pattern) {
		return new NumberBuilder(MockRandomGenerator.number(), pattern);
	}
}