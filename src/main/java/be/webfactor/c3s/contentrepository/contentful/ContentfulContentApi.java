package be.webfactor.c3s.contentrepository.contentful;

import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.CDAResourceNotFoundException;

import be.webfactor.c3s.contentrepository.domain.ContentApi;
import be.webfactor.c3s.contentrepository.domain.ContentItem;
import be.webfactor.c3s.contentrepository.domain.QueryBuilder;

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