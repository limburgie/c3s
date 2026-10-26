package be.webfactor.c3s.siteassetstore.domain;

import be.webfactor.c3s.contentrepository.domain.QueryBuilder;

/**
 * A filter from the site configuration. The type is kept as configured, so an invalid filter only fails when it is
 * validated or applied instead of when the site configuration is read.
 */
public record SitemapFilter(String type, String field, String value) {

	public void validate() {
		SitemapFilterType.fromName(type).validate(field, value);
	}

	public QueryBuilder applyTo(QueryBuilder queryBuilder) {
		SitemapFilterType filterType = SitemapFilterType.fromName(type);
		filterType.validate(field, value);

		return filterType.apply(queryBuilder, field, value);
	}
}
