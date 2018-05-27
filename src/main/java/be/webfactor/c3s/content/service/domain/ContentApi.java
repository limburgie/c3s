package be.webfactor.c3s.content.service.domain;

public interface ContentApi {

	/**
	 * Returns API agnostic query builder for the specified content type.
	 */
	QueryBuilder query(String type);

	/**
	 * Returns native API object.
	 */
	Object nativeApi();
}
