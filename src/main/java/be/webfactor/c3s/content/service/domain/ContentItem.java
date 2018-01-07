package be.webfactor.c3s.content.service.domain;

import java.util.List;

public interface ContentItem extends FieldContainer {

	/**
	 * Returns the group items in this content item with the given field name.
	 */
	List<FieldContainer> getGroup(String fieldName);
}
