package be.webfactor.c3s.siteassetstore.domain;

import be.webfactor.c3s.contentrepository.domain.ContentApi;
import be.webfactor.c3s.contentrepository.domain.QueryBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Describes the detail pages a page serves through a URL parameter, so the sitemap can list them: one URL per content
 * item of the given content type, with the value of the param field as the URL segment after the page's friendly URL.
 */
public record SitemapSettings(String contentType, String paramField, List<SitemapFilter> filters) {

	/**
	 * Throws an {@link IllegalArgumentException} if a required key is missing or a filter is invalid.
	 */
	public void validate() {
		if (StringUtils.isBlank(contentType)) {
			throw new IllegalArgumentException("Sitemap settings require a contentType");
		}
		if (StringUtils.isBlank(paramField)) {
			throw new IllegalArgumentException("Sitemap settings require a paramField");
		}
		filters.forEach(SitemapFilter::validate);
	}

	/**
	 * Returns a query for the content type with all filters applied in order.
	 */
	public QueryBuilder query(ContentApi api) {
		QueryBuilder queryBuilder = api.query(contentType);

		for (SitemapFilter filter : filters) {
			queryBuilder = filter.applyTo(queryBuilder);
		}

		return queryBuilder;
	}
}
