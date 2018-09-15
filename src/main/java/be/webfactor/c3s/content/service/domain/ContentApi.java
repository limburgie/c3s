package be.webfactor.c3s.content.service.domain;

public interface ContentApi {

	/**
	 * Returns API agnostic query builder for the specified content type.
	 */
	QueryBuilder query(String type);

	/**
	 * Finds the content item identified by the given ID, or null if no such item exists.
	 */
	ContentItem findById(String id);

	/**
	 * Returns native API object.
	 */
	Object nativeApi();
}
