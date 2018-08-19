package be.webfactor.c3s.content.service.domain;

import java.util.List;

public interface QueryBuilder {

	/**
	 * Only return content items which have the given value for the given field name.
	 */
	QueryBuilder with(String field, String value);

	/**
	 * Only return content items which are linked with the given content item for the given field name.
	 */
	QueryBuilder with(String field, ContentItem value);

	/**
	 * Only return content items with a date value, specified by the given field name, in the past.
	 * If the includingToday flag is false, only dates before the start of today's date are included.
	 */
	QueryBuilder withDateInPast(String field, boolean includingToday);

	/**
	 * Only return content items with a date value, specified by the given field name, in the future.
	 * If the includingToday flag is false, only dates after the end of today's date are included.
	 */
	QueryBuilder withDateInFuture(String field, boolean includingToday);

	/**
	 * Only return content items with today's date (day, month and year) inside the specified field.
	 */
	QueryBuilder withDateToday(String field);

	/**
	 * Order results by the given field name.
	 */
	QueryBuilder orderByAsc(String fieldName);

	/**
	 * Reversely results by the given field name.
	 */
	QueryBuilder orderByDesc(String fieldName);

	/**
	 * Count the number of items matching the query.
	 */
	int count();

	/**
	 * Find all items matching the query. List is empty if no items were found.
	 */
	List<? extends ContentItem> findAll();

	/**
	 * Find the first items matching the query, returning at most {limit} results.
	 */
	List<? extends ContentItem> findAll(int limit);

	/**
	 * Find at most {size} results, starting from result {size}*{page}+1.
	 */
	List<? extends ContentItem> findAll(int page, int size);

	/**
	 * Find first item matching the query. Returns null if no items were found.
	 */
	ContentItem findFirst();

	/**
	 * Find all items matching the query in random order.
	 */
	List<? extends ContentItem> findRandom();

	/**
	 * Find all items matching the query in random order, returning at most {limit} results.
	 */
	List<? extends ContentItem> findRandom(int limit);
}