package be.webfactor.c3s.contentrepository.domain;

public interface ImageField {

	/**
	 * Gets the URL of this image field, to be used inside an <img> tag.
	 */
	String getUrl();

	/**
	 * Gets the optimized/cropped URL of this image field, to be used inside an <img> tag.
	 */
	String getFinalUrl();

	/**
	 * Gets the alternative text for this image field, or null if none exists.
	 */
	String getAlt();
}
