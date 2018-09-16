package be.webfactor.c3s.content.service.graphcms;

import be.webfactor.c3s.content.service.domain.ContentApi;
import be.webfactor.c3s.content.service.domain.ContentItem;
import be.webfactor.c3s.content.service.domain.QueryBuilder;

public class GraphCmsContentApi implements ContentApi {

	private GraphCmsClient client;

	GraphCmsContentApi(GraphCmsClient client) {
		this.client = client;
	}

	public QueryBuilder query(String type) {
		return new GraphCmsQueryBuilder(client, type);
	}

	public ContentItem findById(String id) {
		throw new UnsupportedOperationException();
	}

	public Object nativeApi() {
		return client;
	}
}