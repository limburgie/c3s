package be.webfactor.c3s.contentrepository.domain;

import java.util.List;

public interface MixedContentField {

	/**
	 * Returns the sequential list of items contained in this mixed content field.
	 */
	List<MixedContentItem> getItems();
}
