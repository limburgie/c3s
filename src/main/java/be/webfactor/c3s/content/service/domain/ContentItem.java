package be.webfactor.c3s.content.service.domain;

import java.util.List;

public interface ContentItem extends FieldContainer {

	/**
	 * Returns the group items in this content item with the given field name.
	 */
	List<FieldContainer> getGroup(String fieldName);

	/**
	 * Returns the UID for this content item, which uniquely identifies this content item within its content type.
	 */
	String getUid();

	/**
	 * Returns the ID for this content item, which uniquely identifies this content item within the whole repository.
	 */
	String getId();

	/**
	 * Returns the edit URL of this content item. This URL will redirect to the edit page inside the content repository's webapp.
	 * @return
	 */
	String getEditUrl();

	/**
	 * Returns the create date for this content item, formatted using the given date pattern.
	 */
	DateBuilder getCreated(String pattern);

	/**
	 * Returns the modified date for this content item, formatted using the given date pattern.
	 */
	DateBuilder getModified(String pattern);
}
