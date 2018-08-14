package be.webfactor.c3s.content.service.graphcms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import be.webfactor.c3s.content.service.ContentService;
import be.webfactor.c3s.content.service.domain.ContentApi;
import be.webfactor.c3s.repository.RepositoryConnection;
import be.webfactor.c3s.repository.RepositoryType;

@Service
@Scope("request")
public class GraphCmsContentService implements ContentService {

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	private ContentApi api;

	public void initialize(RepositoryConnection connection) {
		RestTemplate restTemplate = restTemplateBuilder.rootUri(connection.getRepositoryId()).build();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		if (connection.getAccessToken() != null) {
			httpHeaders.add("Authorization", "Bearer " + connection.getAccessToken());
		}

		api = new GraphCmsContentApi(restTemplate, httpHeaders);
	}

	public ContentApi getApi() {
		return api;
	}

	public RepositoryType getType() {
		return RepositoryType.GRAPHCMS;
	}
}
