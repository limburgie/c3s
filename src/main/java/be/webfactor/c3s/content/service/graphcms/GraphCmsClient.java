package be.webfactor.c3s.content.service.graphcms;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import be.webfactor.c3s.repository.RepositoryConnection;

public class GraphCmsClient {

	private RestTemplate restTemplate;
	private HttpHeaders httpHeaders;

	GraphCmsClient(RepositoryConnection connection) {
		restTemplate = new RestTemplateBuilder().rootUri(connection.getRepositoryId()).build();

		httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		if (connection.getAccessToken() != null) {
			httpHeaders.add("Authorization", "Bearer " + connection.getAccessToken());
		}
	}

	public JsonObject execute(String query) {
		JsonObject requestObject = new JsonObject();
		requestObject.addProperty("query", query);
		requestObject.addProperty("variables", (String) null);

		HttpEntity<String> requestEntity = new HttpEntity<>(requestObject.toString(), httpHeaders);

		ResponseEntity<String> responseEntity = restTemplate.exchange("/", HttpMethod.POST, requestEntity, String.class);
		String response = responseEntity.getBody();

		return response == null ? new JsonObject() : new JsonParser().parse(response).getAsJsonObject();
	}
}
