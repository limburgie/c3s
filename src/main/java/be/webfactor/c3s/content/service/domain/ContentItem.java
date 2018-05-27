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
}
