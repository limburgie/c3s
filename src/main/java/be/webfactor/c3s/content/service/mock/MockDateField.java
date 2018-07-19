package be.webfactor.c3s.content.service.mock;

import java.time.LocalDateTime;

import be.webfactor.c3s.content.service.domain.DateBuilder;
import be.webfactor.c3s.content.service.domain.DateField;

public class MockDateField implements DateField {

	public DateBuilder format(String pattern) {
		return new DateBuilder(LocalDateTime.now(), pattern);
	}

	public boolean isEmpty() {
		return false;
	}
}
