package be.webfactor.c3s.content.service.contentful;

import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.CDAResourceNotFoundException;

import be.webfactor.c3s.content.service.domain.ContentApi;
import be.webfactor.c3s.content.service.domain.ContentItem;
import be.webfactor.c3s.content.service.domain.QueryBuilder;

public class ContentfulContentApi implements ContentApi {

	private CDAClient cdaClient;

	ContentfulContentApi(CDAClient cdaClient) {
		this.cdaClient = cdaClient;
	}

	public QueryBuilder query(String type) {
		return new ContentfulQueryBuilder(cdaClient, type);
	}

	public ContentItem findById(String id) {
		try {
			return new ContentfulContentItem(cdaClient.fetch(CDAEntry.class).one(id));
		} catch (CDAResourceNotFoundException e) {
			return null;
		}
	}

	public Object nativeApi() {
		return cdaClient;
	}
}