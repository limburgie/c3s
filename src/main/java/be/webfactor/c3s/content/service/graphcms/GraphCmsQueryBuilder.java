package be.webfactor.c3s.content.service.graphcms;

import java.util.ArrayList;
import java.util.List;

import org.atteo.evo.inflector.English;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import be.webfactor.c3s.content.service.domain.ContentItem;
import be.webfactor.c3s.content.service.domain.QueryBuilder;

public class GraphCmsQueryBuilder implements QueryBuilder {

	private RestTemplate restTemplate;
	private MultiValueMap<String, String> httpHeaders;
	private String type;

	GraphCmsQueryBuilder(RestTemplate restTemplate, MultiValueMap<String, String> httpHeaders, String type) {
		this.restTemplate = restTemplate;
		this.httpHeaders = httpHeaders;
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
		return null;
	}

	public QueryBuilder orderByDesc(String fieldName) {
		return null;
	}

	public QueryBuilder shuffle() {
		return null;
	}

	public int count() {
		return 0;
	}

	public List<GraphCmsContentItem> findAll() {
		return findAll(100);
	}

	public List<GraphCmsContentItem> findAll(int limit) {
		return findAll(1, limit);
	}

	public List<GraphCmsContentItem> findAll(int page, int size) {
		JsonObject requestObject = new JsonObject();
		requestObject.addProperty("query", "{ " + English.plural(type) + "(first: " + size + ", skip: " + (page-1) * size + ") { id } }");
		requestObject.addProperty("variables", (String) null);

		HttpEntity<String> requestEntity = new HttpEntity<>(requestObject.toString(), httpHeaders);

		ResponseEntity<String> responseEntity = restTemplate.exchange("/", HttpMethod.POST, requestEntity, String.class);
		String response = responseEntity.getBody();

		JsonObject responseObject = new JsonParser().parse(response).getAsJsonObject();
		JsonObject dataObject = responseObject.getAsJsonObject("data");
		JsonArray items = dataObject.getAsJsonArray(English.plural(type));

		List<GraphCmsContentItem> results = new ArrayList<>();
		for (int i = 0; i < items.size(); i++) {
			JsonObject item = items.get(i).getAsJsonObject();
			String id = item.get("id").getAsString();

			results.add(new GraphCmsContentItem(id));
		}

		return results;
	}

	public ContentItem findFirst() {
		return null;
	}
}
