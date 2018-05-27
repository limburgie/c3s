package be.webfactor.c3s.content.service.mock;

import java.util.List;

import be.webfactor.c3s.content.service.domain.ContentItem;
import be.webfactor.c3s.content.service.domain.FieldContainer;
import be.webfactor.c3s.content.service.domain.QueryBuilder;

public class MockQueryBuilder implements QueryBuilder {

	private String type;

	MockQueryBuilder(String type) {
		this.type = type;
	}

	public QueryBuilder with(String field, String value) {
		return this;
	}

	public QueryBuilder with(String field, ContentItem value) {
		return this;
	}

	public QueryBuilder withDateInPast(String field, boolean includingToday) {
		return this;
	}

	public QueryBuilder withDateInFuture(String field, boolean includingToday) {
		return this;
	}

	public QueryBuilder withDateToday(String field) {
		return this;
	}

	public QueryBuilder orderByAsc(String fieldName) {
		return this;
	}

	public QueryBuilder orderByDesc(String fieldName) {
		return this;
	}

	public QueryBuilder shuffle() {
		return this;
	}

	public int count() {
		return MockRandomGenerator.integer();
	}

	public List<ContentItem> findAll() {
		return MockRandomGenerator.contentItemList(type, 20);
	}

	public List<ContentItem> findAll(int limit) {
		return MockRandomGenerator.contentItemList(type, limit);
	}

	public List<ContentItem> findAll(int page, int size) {
		return findAll(size);
	}

	public ContentItem findFirst() {
		return MockRandomGenerator.contentItem(type);
	}
}
