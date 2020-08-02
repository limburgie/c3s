package be.webfactor.c3s.content.service.contentful;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.FetchQuery;
import com.contentful.java.cda.QueryOperation;

import be.webfactor.c3s.content.service.domain.ContentItem;
import be.webfactor.c3s.content.service.domain.QueryBuilder;

public class ContentfulQueryBuilder implements QueryBuilder {

	private static final int MAX_ITEMS = 1000;

	private FetchQuery<CDAEntry> fetchQuery;

	ContentfulQueryBuilder(CDAClient cdaClient, String type) {
		fetchQuery = cdaClient.fetch(CDAEntry.class).withContentType(type);
	}

	public QueryBuilder with(String field, String value) {
		fetchQuery.where(fieldsPrefix(field), value);

		return this;
	}
	public QueryBuilder with(String field, ContentItem value) {
		fetchQuery.where(fieldsPrefix(field) + ".sys.id", value.getId());

		return this;
	}

	public QueryBuilder search(String keyword) {
		throw new UnsupportedOperationException();
	}

	public QueryBuilder withTag(String tag) {
		throw new UnsupportedOperationException();
	}

	public QueryBuilder withDateInPast(String field) {
		return withDateInPast(field, true);
	}

	public QueryBuilder withDateBeforeToday(String field) {
		return withDateInPast(field, false);
	}

	private QueryBuilder withDateInPast(String field, boolean includingToday) {
		fetchQuery.where(fieldsPrefix(field), QueryOperation.IsEarlierThan, formatDate(includingToday ? ZonedDateTime.now() : getStartOfDay(ZonedDateTime.now())));

		return this;
	}

	public QueryBuilder withDateInFuture(String field) {
		return withDateInFuture(field, true);
	}

	public QueryBuilder withDateAfterToday(String field) {
		return withDateInFuture(field, false);
	}

	private QueryBuilder withDateInFuture(String field, boolean includingToday) {
		fetchQuery.where(fieldsPrefix(field), QueryOperation.IsLaterThan, formatDate(includingToday ? ZonedDateTime.now() : getStartOfDay(ZonedDateTime.now().plusDays(1))));

		return this;
	}

	public QueryBuilder withDateToday(String field) {
		fetchQuery.where(fieldsPrefix(field), QueryOperation.IsLaterOrAt, formatDate(getStartOfDay(ZonedDateTime.now())));
		fetchQuery.where(fieldsPrefix(field), QueryOperation.IsEarlierOrAt, formatDate(getStartOfDay(ZonedDateTime.now().plusDays(1))));

		return this;
	}

	private ZonedDateTime getStartOfDay(ZonedDateTime zonedDateTime) {
		return zonedDateTime.toLocalDate().atStartOfDay(ZoneId.systemDefault());
	}

	private String formatDate(ZonedDateTime zonedDateTime) {
		return zonedDateTime.toLocalDate().format(DateTimeFormatter.ISO_DATE);
	}

	public QueryBuilder orderByAsc(String fieldName) {
		fetchQuery.orderBy(fieldsPrefix(fieldName));

		return this;
	}

	public QueryBuilder orderByDesc(String fieldName) {
		fetchQuery.reverseOrderBy(fieldsPrefix(fieldName));

		return this;
	}

	private String fieldsPrefix(String fieldName) {
		return "fields." + fieldName;
	}

	public int count() {
		return fetchQuery.all().total();
	}

	public List<ContentfulContentItem> findAll() {
		return findAll(MAX_ITEMS);
	}

	public List<ContentfulContentItem> findAll(int limit) {
		return findAll(1, limit);
	}

	public List<ContentfulContentItem> findAll(int page, int size) {
		return fetchQuery.limit(size).skip((page-1)*size).all().items().stream().map(cdaResource -> new ContentfulContentItem((CDAEntry) cdaResource)).collect(Collectors.toList());
	}

	public ContentfulContentItem findOne() {
		List<ContentfulContentItem> items = findAll(1);

		return items.isEmpty() ? null : items.get(0);
	}

	public List<ContentfulContentItem> findRandom() {
		return findRandom(MAX_ITEMS);
	}

	public List<ContentfulContentItem> findRandom(int limit) {
		List<ContentfulContentItem> items = findAll();

		Collections.shuffle(items);

		return items.subList(0, Math.min(items.size(), limit));
	}
}