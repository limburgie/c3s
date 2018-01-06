package be.webfactor.c3s.content.service.prismic;

import be.webfactor.c3s.content.service.domain.ContentApi;
import be.webfactor.c3s.content.service.domain.QueryBuilder;
import io.prismic.Api;

public class PrismicContentApi implements ContentApi {

	private Api api;

	PrismicContentApi(Api api) {
		this.api = api;
	}

	public QueryBuilder query() {
		return new PrismicQueryBuilder(api);
	}

	public Object nativeApi() {
		return api;
	}
}
