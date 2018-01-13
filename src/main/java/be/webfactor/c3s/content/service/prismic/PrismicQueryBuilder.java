package be.webfactor.c3s.content.service.prismic;

import java.util.ArrayList;
import java.util.Collections;
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
	private boolean shuffled;

	PrismicQueryBuilder(Api api, String type) {
		this.api = api;
		this.type = type;

		predicates.add(Predicates.at("document.type", type));
	}

	public QueryBuilder with(String field, String value) {
		predicates.add(Predicates.at(docPrefix(field), value));

		return this;
	}

	public QueryBuilder withDateInPast(String field) {
		predicates.add(Predicates.dateBefore(docPrefix(field), DateTime.now()));

		return this;
	}

	public QueryBuilder withDateInFuture(String field) {
		predicates.add(Predicates.dateAfter(docPrefix(field), DateTime.now()));

		return this;
	}

	public QueryBuilder withDateToday(String field) {
		predicates.add(Predicates.dayOfMonth(docPrefix(field), DateTime.now().getDayOfMonth()));
		predicates.add(Predicates.month(docPrefix(field), Predicates.Month.valueOf(DateTime.now().toString("MMMMM").toUpperCase())));
		predicates.add(Predicates.year(docPrefix(field), DateTime.now().getYear()));

		return this;
	}

	public QueryBuilder orderByAsc(String fieldName) {
		return addOrdering(fieldName);
	}

	public QueryBuilder orderByDesc(String fieldName) {
		return addOrdering(fieldName + " desc");
	}

	public QueryBuilder shuffle() {
		shuffled = true;

		return this;
	}

	private QueryBuilder addOrdering(String ordering) {
		orderings.add(docPrefix(ordering));

		return this;
	}

	private String docPrefix(String field) {
		return "my." + type + "." + field;
	}

	public int count() {
		return buildQuery().submit().getTotalResultsSize();
	}

	public List<PrismicContentItem> findAll() {
		return findAll(100);
	}

	public List<PrismicContentItem> findAll(int limit) {
		return findAll(1, limit);
	}

	public List<PrismicContentItem> findAll(int page, int size) {
		Form.SearchForm searchForm = buildQueryWithOrderings();

		if (!shuffled) {
			searchForm.page(page).pageSize(size);
		}

		List<PrismicContentItem> results = searchForm.submit().getResults().stream().map(document -> new PrismicContentItem(document, api)).collect(Collectors.toList());

		if (shuffled) {
			Collections.shuffle(results);
			results = results.subList((page-1) * size, Math.min(results.size(), page * size));
		}

		return results;
	}

	public ContentItem findFirst() {
		List<PrismicContentItem> items = findAll(1);

		return items.isEmpty() ? null : items.get(0);
	}

	private Form.SearchForm buildQueryWithOrderings() {
		return buildQuery().orderings(buildOrderings());
	}

	private Form.SearchForm buildQuery() {
		return api.query(predicates.toArray(new Predicate[predicates.size()]));
	}

	private String buildOrderings() {
		return StringUtils.join(orderings, ", ");
	}
}