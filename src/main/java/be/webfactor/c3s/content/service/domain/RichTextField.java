package be.webfactor.c3s.content.service.domain;

public interface RichTextField extends ValueWrapper {

	/**
	 * Outputs the rich text as HTML.
	 */
	String getHtml();

	/**
	 * Abbreviates the HTML stripped content of this field to retain at most {length} characters.
	 */
	String abbreviate(int length);
}
