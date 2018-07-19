package be.webfactor.c3s.content.service.domain;

public interface NumberField extends ValueWrapper {

	/**
	 * Formats this number using the given pattern.
	 */
	String format(String pattern);

	/**
	 * Formats this number using the given pattern and locale.
	 */
	String format(String pattern, String locale);
}
