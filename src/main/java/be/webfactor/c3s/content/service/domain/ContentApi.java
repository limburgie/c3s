package be.webfactor.c3s.content.service.domain;

public interface ContentApi {

	/**
	 * Returns API agnostic query builder.
	 */
	QueryBuilder query();

	/**
	 * Returns native API object.
	 */
	Object nativeApi();
}
