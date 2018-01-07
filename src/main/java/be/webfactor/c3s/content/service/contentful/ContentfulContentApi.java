package be.webfactor.c3s.content.service.contentful;

import com.contentful.java.cda.CDAClient;

import be.webfactor.c3s.content.service.domain.ContentApi;
import be.webfactor.c3s.content.service.domain.QueryBuilder;

public class ContentfulContentApi implements ContentApi {

	private CDAClient cdaClient;

	ContentfulContentApi(CDAClient cdaClient) {
		this.cdaClient = cdaClient;
	}

	public QueryBuilder query() {
		return new ContentfulQueryBuilder(cdaClient);
	}

	public Object nativeApi() {
		return cdaClient;
	}
}