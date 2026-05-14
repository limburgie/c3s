package be.webfactor.c3s.contentrepository.mock;

import be.webfactor.c3s.contentrepository.domain.ContentApi;
import be.webfactor.c3s.contentrepository.domain.ContentItem;
import be.webfactor.c3s.contentrepository.domain.QueryBuilder;

public class MockContentApi implements ContentApi {

	public QueryBuilder query(String type) {
		return new MockQueryBuilder(type);
	}

	public ContentItem findById(String id) {
		return new MockContentItem(null);
	}

	public Object nativeApi() {
		return null;
	}
}
