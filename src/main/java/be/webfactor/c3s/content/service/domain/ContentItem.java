package be.webfactor.c3s.content.service.domain;

public interface ContentItem extends FieldContainer {

	/**
	 * Returns the group field in this content item with the given field name.
	 */
	GroupField getGroup(String fieldName);
}
