package be.webfactor.c3s.content.service.graphcms;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.atteo.evo.inflector.English;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import be.webfactor.c3s.content.service.domain.ContentItem;
import be.webfactor.c3s.content.service.domain.QueryBuilder;

public class GraphCmsQueryBuilder implements QueryBuilder {

	private static final int MAX_ITEMS = 1000;

	private GraphCmsClient client;
	private String type;
	private String orderBy;
	private Map<String, String> whereMappings = new LinkedHashMap<>();

	GraphCmsQueryBuilder(GraphCmsClient client, String type) {
		this.client = client;
		this.type = type;
	}

	public QueryBuilder with(String field, String value) {
		whereMappings.put(field, "\"" + value + "\"");

		return this;
	}

	public QueryBuilder with(String field, ContentItem value) {
		whereMappings.put(field, "{id: \"" + value.getId() + "\"}");

		return this;
	}

	public QueryBuilder withDateInPast(String field, boolean includingToday) {
		throw new UnsupportedOperationException();
	}

	public QueryBuilder withDateInFuture(String field, boolean includingToday) {
		throw new UnsupportedOperationException();
	}

	public QueryBuilder withDateToday(String field) {
		throw new UnsupportedOperationException();
	}

	public QueryBuilder orderByAsc(String fieldName) {
		orderBy = fieldName + "_ASC";

		return this;
	}

	public QueryBuilder orderByDesc(String fieldName) {
		orderBy = fieldName + "_DESC";

		return this;
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
		return findAll(MAX_ITEMS);
	}

	public List<GraphCmsContentItem> findAll(int limit) {
		return findAll(1, limit);
	}

	public List<GraphCmsContentItem> findAll(int page, int size) {
		String order = orderBy == null ? "" : ", orderBy: " + orderBy;
		String where = "";

		if (!whereMappings.isEmpty()) {
			List<String> whereClauses = whereMappings.entrySet().stream().map(entry -> "{ " + entry.getKey() + ": " + entry.getValue() + "}").collect(Collectors.toList());

			where = ", where: {AND: [" + StringUtils.join(whereClauses, ", ") + "]}";
		}

		String query = "{ " + English.plural(type) + "(first: " + size + ", skip: " + (page - 1) * size + where + order + ") { id } }";

		JsonObject response = client.execute(query);
		JsonObject dataObject = response.getAsJsonObject("data");
		JsonArray items = dataObject.getAsJsonArray(English.plural(type));

		List<GraphCmsContentItem> results = new ArrayList<>();
		for (int i = 0; i < items.size(); i++) {
			JsonObject item = items.get(i).getAsJsonObject();
			String id = item.get("id").getAsString();

			results.add(new GraphCmsContentItem(id, type, client));
		}

		return results;
	}

	public GraphCmsContentItem findFirst() {
		List<GraphCmsContentItem> items = findAll(1);

		return items.isEmpty() ? null : items.get(0);
	}

	public List<GraphCmsContentItem> findRandom() {
		return findRandom(MAX_ITEMS);
	}

	public List<GraphCmsContentItem> findRandom(int limit) {
		List<GraphCmsContentItem> items = findAll();

		Collections.shuffle(items);

		return items.subList(0, Math.min(items.size(), limit));
	}
}