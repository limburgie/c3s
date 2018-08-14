package be.webfactor.c3s.content.service.graphcms;

import java.util.ArrayList;
import java.util.List;

import org.atteo.evo.inflector.English;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import be.webfactor.c3s.content.service.domain.ContentItem;
import be.webfactor.c3s.content.service.domain.QueryBuilder;

public class GraphCmsQueryBuilder implements QueryBuilder {

	private GraphCmsClient client;
	private String type;
	private String orderBy;

	GraphCmsQueryBuilder(GraphCmsClient client, String type) {
		this.client = client;
		this.type = type;
	}

	public QueryBuilder with(String field, String value) {
		return null;
	}

	public QueryBuilder with(String field, ContentItem value) {
		return null;
	}

	public QueryBuilder withDateInPast(String field, boolean includingToday) {
		return null;
	}

	public QueryBuilder withDateInFuture(String field, boolean includingToday) {
		return null;
	}

	public QueryBuilder withDateToday(String field) {
		return null;
	}

	public QueryBuilder orderByAsc(String fieldName) {
		orderBy = fieldName + "_ASC";

		return this;
	}

	public QueryBuilder orderByDesc(String fieldName) {
		orderBy = fieldName + "_DESC";

		return this;
	}

	public QueryBuilder shuffle() {
		return null;
	}

	public int count() {
		String query = "{ " + English.plural(type) + "Connection { aggregate { count } } }";
		JsonObject response = client.execute(query);
		JsonObject dataObject = response.getAsJsonObject("data");
		JsonObject typeConnectionObject = dataObject.getAsJsonObject(English.plural(type) + "Connection");
		JsonObject aggregateObject = typeConnectionObject.getAsJsonObject("aggregate");

		return aggregateObject.get("count").getAsInt();
	}

	public List<GraphCmsContentItem> findAll() {
		return findAll(1000);
	}

	public List<GraphCmsContentItem> findAll(int limit) {
		return findAll(1, limit);
	}

	public List<GraphCmsContentItem> findAll(int page, int size) {
		String order = orderBy == null ? "" : ", orderBy: " + orderBy;
		String query = "{ " + English.plural(type) + "(first: " + size + ", skip: " + (page - 1) * size + order + ") { id } }";
		JsonObject response = client.execute(query);
		JsonObject dataObject = response.getAsJsonObject("data");
		JsonArray items = dataObject.getAsJsonArray(English.plural(type));

		List<GraphCmsContentItem> results = new ArrayList<>();
		for (int i = 0; i < items.size(); i++) {
			JsonObject item = items.get(i).getAsJsonObject();
			String id = item.get("id").getAsString();

			results.add(new GraphCmsContentItem(id));
		}

		return results;
	}

	public GraphCmsContentItem findFirst() {
		List<GraphCmsContentItem> items = findAll(1);

		return items.isEmpty() ? null : items.get(0);
	}
}