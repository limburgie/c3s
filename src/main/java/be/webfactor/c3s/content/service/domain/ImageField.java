package be.webfactor.c3s.content.service.domain;

public interface ImageField extends ValueWrapper {

	/**
	 * Gets the URL of this image field, to be used inside an <img> tag.
	 */
	String getUrl();

	/**
	 * Gets the alternative text for this image field, or null if none exists.
	 */
	String getAlt();
}
