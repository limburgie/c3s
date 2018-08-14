package be.webfactor.c3s.content.service.graphcms;

import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import be.webfactor.c3s.content.service.domain.ContentApi;
import be.webfactor.c3s.content.service.domain.QueryBuilder;

public class GraphCmsContentApi implements ContentApi {

	private RestTemplate restTemplate;
	private MultiValueMap<String, String> httpHeaders;

	GraphCmsContentApi(RestTemplate restTemplate, MultiValueMap<String, String> httpHeaders) {
		this.restTemplate = restTemplate;
		this.httpHeaders = httpHeaders;
	}

	public QueryBuilder query(String type) {
		return new GraphCmsQueryBuilder(restTemplate, httpHeaders, type);
	}

	public Object nativeApi() {
		throw new UnsupportedOperationException();
	}
}
