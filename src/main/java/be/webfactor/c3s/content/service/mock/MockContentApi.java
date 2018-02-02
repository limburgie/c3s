package be.webfactor.c3s.content.service.mock;

import be.webfactor.c3s.content.service.domain.ContentApi;
import be.webfactor.c3s.content.service.domain.QueryBuilder;

public class MockContentApi implements ContentApi {

	public QueryBuilder query(String type) {
		return new MockQueryBuilder(type);
	}

	public Object nativeApi() {
		return null;
	}
}
