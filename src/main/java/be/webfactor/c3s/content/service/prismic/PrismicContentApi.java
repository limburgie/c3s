package be.webfactor.c3s.content.service.prismic;

import be.webfactor.c3s.content.service.domain.ContentApi;
import be.webfactor.c3s.content.service.domain.ContentItem;
import be.webfactor.c3s.content.service.domain.QueryBuilder;
import io.prismic.Api;
import io.prismic.Document;

public class PrismicContentApi implements ContentApi {

	private Api api;

	PrismicContentApi(Api api) {
		this.api = api;
	}

	public QueryBuilder query(String type) {
		return new PrismicQueryBuilder(api, type);
	}

	public ContentItem findById(String id) {
		Document document = api.getByID(id);

		if (document == null) {
			return null;
		}

		return new PrismicContentItem(document, api);
	}

	public Object nativeApi() {
		return api;
	}
}