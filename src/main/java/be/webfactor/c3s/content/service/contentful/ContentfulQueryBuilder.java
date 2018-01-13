package be.webfactor.c3s.content.service.contentful;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.FetchQuery;
import com.contentful.java.cda.QueryOperation;

import be.webfactor.c3s.content.service.domain.QueryBuilder;

public class ContentfulQueryBuilder implements QueryBuilder {

	private FetchQuery<CDAEntry> fetchQuery;
	private boolean shuffled;

	ContentfulQueryBuilder(CDAClient cdaClient, String type) {
		fetchQuery = cdaClient.fetch(CDAEntry.class).withContentType(type);
	}

	public QueryBuilder with(String field, String value) {
		fetchQuery.where(fieldsPrefix(field), value);

		return this;
	}

	public QueryBuilder withDateInPast(String field) {
		fetchQuery.where(fieldsPrefix(field), QueryOperation.IsEarlierThan, DateTime.now().toString());

		return this;
	}

	public QueryBuilder withDateInFuture(String field) {
		fetchQuery.where(fieldsPrefix(field), QueryOperation.IsLaterThan, DateTime.now().toString());

		return this;
	}

	public QueryBuilder withDateToday(String field) {
		fetchQuery.where(fieldsPrefix(field), QueryOperation.IsLaterOrAt, DateTime.now().withTimeAtStartOfDay().toString());
		fetchQuery.where(fieldsPrefix(field), QueryOperation.IsEarlierOrAt, DateTime.now().plusDays(1).withTimeAtStartOfDay().toString());

		return this;
	}

	public QueryBuilder orderByAsc(String fieldName) {
		fetchQuery.orderBy(fieldsPrefix(fieldName));

		return this;
	}

	public QueryBuilder orderByDesc(String fieldName) {
		fetchQuery.reverseOrderBy(fieldsPrefix(fieldName));

		return this;
	}

	public QueryBuilder shuffle() {
		shuffled = true;

		return this;
	}

	private String fieldsPrefix(String fieldName) {
		return "fields." + fieldName;
	}

	public int count() {
		return fetchQuery.all().total();
	}

	public List<ContentfulContentItem> findAll() {
		return findAll(1000);
	}

	public List<ContentfulContentItem> findAll(int limit) {
		return findAll(1, limit);
	}

	public List<ContentfulContentItem> findAll(int page, int size) {
		if (!shuffled) {
			fetchQuery.limit(size).skip((page-1)*size);
		}

		List<ContentfulContentItem> results = fetchQuery.all().items().stream().map(cdaResource -> new ContentfulContentItem((CDAEntry) cdaResource)).collect(Collectors.toList());

		if (shuffled) {
			Collections.shuffle(results);
			results = results.subList((page-1) * size, Math.min(results.size(), page * size));
		}

		return results;
	}

	public ContentfulContentItem findFirst() {
		List<ContentfulContentItem> items = findAll(1);

		return items.isEmpty() ? null : items.get(0);
	}
}
