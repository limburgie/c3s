package be.webfactor.c3s.content.service.prismic;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import be.webfactor.c3s.content.service.domain.ContentItem;
import be.webfactor.c3s.content.service.domain.QueryBuilder;
import be.webfactor.c3s.master.domain.LocationThreadLocal;
import io.prismic.*;

public class PrismicQueryBuilder implements QueryBuilder {

	private static final int MAX_ITEMS = 100;

	private Api api;
	private List<Predicate> predicates = new ArrayList<>();
	private List<String> orderings = new ArrayList<>();
	private String type;

	PrismicQueryBuilder(Api api, String type) {
		this.api = api;
		this.type = type;

		predicates.add(Predicates.at("document.type", type));
	}

	public QueryBuilder with(String field, String value) {
		predicates.add(Predicates.at(docPrefix(field), value));

		return this;
	}

	public QueryBuilder with(String field, ContentItem value) {
		predicates.add(Predicates.at(docPrefix(field), value.getId()));

		return this;
	}

	public QueryBuilder search(String keyword) {
		predicates.add(Predicates.fulltext("document", keyword));

		return this;
	}

	public QueryBuilder withTag(String tag) {
		predicates.add(Predicates.any("document.tags", Arrays.asList(tag, tag.toLowerCase())));

		return this;
	}

	public QueryBuilder withDateInPast(String field) {
		return withDateInPast(field, true);
	}

	public QueryBuilder withDateBeforeToday(String field) {
		return withDateInPast(field, false);
	}

	private QueryBuilder withDateInPast(String field, boolean includingToday) {
		predicates.add(Predicates.dateBefore(docPrefix(field), includingToday ? ZonedDateTime.now() : ZonedDateTime.now().toLocalDate().atStartOfDay(ZoneId.systemDefault())));

		return this;
	}

	public QueryBuilder withDateInFuture(String field) {
		return withDateInFuture(field, true);
	}

	public QueryBuilder withDateAfterToday(String field) {
		return withDateInFuture(field, false);
	}

	private QueryBuilder withDateInFuture(String field, boolean includingToday) {
		predicates.add(Predicates.dateAfter(docPrefix(field), includingToday ? ZonedDateTime.now() : ZonedDateTime.now().plusDays(1).toLocalDate().atStartOfDay(ZoneId.systemDefault())));

		return this;
	}

	public QueryBuilder withDateToday(String field) {
		predicates.add(Predicates.dayOfMonth(docPrefix(field), LocalDateTime.now().getDayOfMonth()));
		predicates.add(Predicates.month(docPrefix(field), Predicates.Month.valueOf(LocalDateTime.now().getMonth().toString())));
		predicates.add(Predicates.year(docPrefix(field), LocalDateTime.now().getYear()));

		return this;
	}

	public QueryBuilder orderByAsc(String fieldName) {
		return addOrdering(fieldName);
	}

	public QueryBuilder orderByDesc(String fieldName) {
		return addOrdering(fieldName + " desc");
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
		return findAll(MAX_ITEMS);
	}

	public List<PrismicContentItem> findAll(int limit) {
		return findAll(1, limit);
	}

	public List<PrismicContentItem> findAll(int page, int size) {
		return buildQueryWithOrderings().page(page).pageSize(size).submit().getResults().stream().map(document -> new PrismicContentItem(document, api)).collect(Collectors.toList());
	}

	public PrismicContentItem findOne() {
		List<PrismicContentItem> items = findAll(1);

		return items.isEmpty() ? null : items.get(0);
	}

	public List<PrismicContentItem> findRandom() {
		return findRandom(MAX_ITEMS);
	}

	public List<PrismicContentItem> findRandom(int limit) {
		List<PrismicContentItem> items = findAll();

		Collections.shuffle(items);

		return items.subList(0, Math.min(items.size(), limit));
	}

	private Form.SearchForm buildQueryWithOrderings() {
		return buildQuery().orderings(buildOrderings());
	}

	private Form.SearchForm buildQuery() {
		return api.query(predicates.toArray(new Predicate[0])).lang(getLanguage());
	}

	private String getLanguage() {
		Locale locale = LocationThreadLocal.getLocaleContext().getLocale();

		String language = locale.getLanguage();
		String country = locale.getCountry().toLowerCase();

		return String.format("%s-%s", language, country);
	}

	private String buildOrderings() {
		return StringUtils.join(orderings, ", ");
	}
}