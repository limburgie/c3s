package be.webfactor.c3s.content.service.contentful;

import java.util.List;
import java.util.stream.Collectors;

import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.FetchQuery;

import be.webfactor.c3s.content.service.domain.ContentItem;
import be.webfactor.c3s.content.service.domain.QueryBuilder;

public class ContentfulQueryBuilder implements QueryBuilder {

	private FetchQuery<CDAEntry> fetchQuery;
	private boolean randomizeOrder;

	ContentfulQueryBuilder(CDAClient cdaClient) {
		fetchQuery = cdaClient.fetch(CDAEntry.class);
	}

	public QueryBuilder byType(String type) {
		fetchQuery.withContentType(type);

		return this;
	}

	public QueryBuilder with(String field, String value) {
		fetchQuery.where(fieldsPrefix(field), value);

		return this;
	}

	public QueryBuilder withDateInPast(String field) {
		throw new UnsupportedOperationException();
	}

	public QueryBuilder withDateInFuture(String field) {
		throw new UnsupportedOperationException();
	}

	public QueryBuilder orderByAsc(String fieldName) {
		fetchQuery.orderBy(fieldsPrefix(fieldName));

		return this;
	}

	public QueryBuilder orderByDesc(String fieldName) {
		fetchQuery.reverseOrderBy(fieldsPrefix(fieldName));

		return this;
	}

	public QueryBuilder orderRandomly() {
		randomizeOrder = true;

		return this;
	}

	private String fieldsPrefix(String fieldName) {
		return "fields." + fieldName;
	}

	public List<ContentfulContentItem> findAll() {
		return findAll(1000);
	}

	public List<ContentfulContentItem> findAll(int limit) {
		fetchQuery.limit(limit);

		return doFindAll();
	}

	public List<ContentfulContentItem> findAll(int page, int size) {
		fetchQuery.limit(size).skip((page-1)*size);

		return doFindAll();
	}

	private List<ContentfulContentItem> doFindAll() {
		return fetchQuery.all().items().stream().map(cdaResource -> new ContentfulContentItem((CDAEntry) cdaResource)).collect(Collectors.toList());
	}

	public ContentItem findFirst() {
		return fetchQuery.limit(1).all().items().stream().map(cdaResource -> new ContentfulContentItem((CDAEntry) cdaResource)).findFirst().orElse(null);
	}
}
