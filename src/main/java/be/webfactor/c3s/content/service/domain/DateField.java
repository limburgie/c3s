package be.webfactor.c3s.content.service.domain;

public interface DateField {

	/**
	 * Formats the date inside this field using the given pattern.
	 */
	DateBuilder format(String pattern);
}
