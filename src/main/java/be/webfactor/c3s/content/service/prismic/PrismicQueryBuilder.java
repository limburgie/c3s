package be.webfactor.c3s.content.service.prismic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import be.webfactor.c3s.content.service.domain.ContentItem;
import be.webfactor.c3s.content.service.domain.QueryBuilder;
import io.prismic.*;

public class PrismicQueryBuilder implements QueryBuilder {

	private Api api;
	private List<Predicate> predicates = new ArrayList<>();
	private List<String> orderings = new ArrayList<>();
	private String type;
	private boolean randomizeOrder;

	PrismicQueryBuilder(Api api) {
		this.api = api;
	}

	public QueryBuilder byType(String type) {
		this.type = type;

		predicates.add(Predicates.at("document.type", type));

		return this;
	}

	public QueryBuilder with(String field, String value) {
		predicates.add(Predicates.at(docPrefix(field), value));

		return this;
	}

	public QueryBuilder withDateInPast(String field) {
		predicates.add(Predicates.dateBefore(field, DateTime.now()));

		return this;
	}

	public QueryBuilder withDateInFuture(String field) {
		predicates.add(Predicates.dateAfter(field, DateTime.now()));

		return this;
	}

	public QueryBuilder orderByAsc(String fieldName) {
		return addOrdering(fieldName);
	}

	public QueryBuilder orderByDesc(String fieldName) {
		return addOrdering(fieldName + " desc");
	}

	public QueryBuilder orderRandomly() {
		randomizeOrder = true;

		return this;
	}

	private QueryBuilder addOrdering(String ordering) {
		orderings.add(docPrefix(ordering));

		return this;
	}

	private String docPrefix(String field) {
		return "my." + type + "." + field;
	}

	public List<PrismicContentItem> findAll() {
		return findAll(100);
	}

	public List<PrismicContentItem> findAll(int limit) {
		return doFindAll(buildQuery().pageSize(limit));
	}

	public List<PrismicContentItem> findAll(int page, int size) {
		return doFindAll(buildQuery().pageSize(size).page(page));
	}

	private List<PrismicContentItem> doFindAll(Form.SearchForm searchForm) {
		return searchForm.submit().getResults().stream().map(PrismicContentItem::new).collect(Collectors.toList());
	}

	public ContentItem findFirst() {
		List<Document> documents = buildQuery().pageSize(1).submit().getResults();

		return documents.stream().map(PrismicContentItem::new).findFirst().orElse(null);
	}

	private Form.SearchForm buildQuery() {
		return api.query(predicates.toArray(new Predicate[predicates.size()])).orderings(buildOrderings());
	}

	private String buildOrderings() {
		return StringUtils.join(orderings, ", ");
	}
}