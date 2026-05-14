package be.webfactor.c3s.contentrepository.mock;

import java.time.LocalDateTime;

import be.webfactor.c3s.contentrepository.domain.DateBuilder;
import be.webfactor.c3s.contentrepository.domain.DateField;

public class MockDateField implements DateField {

	public DateBuilder format(String pattern) {
		return new DateBuilder(LocalDateTime.now(), pattern);
	}
}