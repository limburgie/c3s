package be.webfactor.c3s.contentrepository.domain;

public interface NumberField {

	/**
	 * Formats this number using the given pattern.
	 */
	NumberBuilder format(String pattern);
}