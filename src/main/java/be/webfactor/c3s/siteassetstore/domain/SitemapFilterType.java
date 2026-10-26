package be.webfactor.c3s.siteassetstore.domain;

import be.webfactor.c3s.contentrepository.domain.QueryBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * The filters a page can apply to the query for its detail pages. Each type maps to one {@link QueryBuilder} method.
 */
public enum SitemapFilterType {

	EQUALS("equals", true, true, QueryBuilder::with),
	TAG("tag", false, true, (queryBuilder, _, value) -> queryBuilder.withTag(value)),
	DATE_IN_PAST("dateInPast", true, false, (queryBuilder, field, _) -> queryBuilder.withDateInPast(field)),
	DATE_BEFORE_TODAY("dateBeforeToday", true, false, (queryBuilder, field, _) -> queryBuilder.withDateBeforeToday(field)),
	DATE_IN_FUTURE("dateInFuture", true, false, (queryBuilder, field, _) -> queryBuilder.withDateInFuture(field)),
	DATE_AFTER_TODAY("dateAfterToday", true, false, (queryBuilder, field, _) -> queryBuilder.withDateAfterToday(field)),
	DATE_TODAY("dateToday", true, false, (queryBuilder, field, _) -> queryBuilder.withDateToday(field));

	private final String name;
	private final boolean requiresField;
	private final boolean requiresValue;
	private final Filter filter;

	SitemapFilterType(String name, boolean requiresField, boolean requiresValue, Filter filter) {
		this.name = name;
		this.requiresField = requiresField;
		this.requiresValue = requiresValue;
		this.filter = filter;
	}

	public static SitemapFilterType fromName(String name) {
		return Arrays.stream(values())
				.filter(type -> type.name.equals(name))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Unknown sitemap filter type: " + name));
	}

	public void validate(String field, String value) {
		if (requiresField && StringUtils.isBlank(field)) {
			throw new IllegalArgumentException("Sitemap filter '" + name + "' requires a field");
		}
		if (requiresValue && value == null) {
			throw new IllegalArgumentException("Sitemap filter '" + name + "' requires a value");
		}
	}

	public QueryBuilder apply(QueryBuilder queryBuilder, String field, String value) {
		return filter.apply(queryBuilder, field, value);
	}

	@FunctionalInterface
	private interface Filter {
		QueryBuilder apply(QueryBuilder queryBuilder, String field, String value);
	}
}
