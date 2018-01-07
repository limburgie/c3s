package be.webfactor.c3s.content.service.domain;

public interface FieldContainer {

	/**
	 * Returns the contents of the text field with the given field name.
	 */
	String getText(String fieldName);

	/**
	 * Returns the image field in this content item with the given field name.
	 */
	ImageField getImage(String fieldName);

	/**
	 * Returns the date field in this content item with the given field name.
	 */
	DateField getDate(String fieldName);

	/**
	 * Returns the number field in this content item with the given field name.
	 */
	NumberField getNumber(String fieldName);

	/**
	 * Returns the content item referenced by the given field name in this content item.
	 */
	ContentItem getReference(String fieldName);
}
