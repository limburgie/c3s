package be.webfactor.c3s.content.service.mock;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.LocaleUtils;

import be.webfactor.c3s.content.service.domain.DateField;

public class MockDateField implements DateField {

	public String format(String pattern) {
		return format(pattern, "en_US");
	}

	public String format(String pattern, String locale) {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern).withLocale(LocaleUtils.toLocale(locale)));
	}

	public boolean isEmpty() {
		return false;
	}
}
