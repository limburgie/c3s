package be.webfactor.c3s.contentrepository.mock;

import be.webfactor.c3s.contentrepository.ContentRepositoryConnection;
import be.webfactor.c3s.contentrepository.ContentRepositoryType;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.contentrepository.ContentRepository;
import be.webfactor.c3s.contentrepository.domain.ContentApi;

@Service
@Scope("request")
public class MockContentRepository implements ContentRepository {

	public void initialize(ContentRepositoryConnection connection) {

	}

	public ContentApi getApi() {
		return new MockContentApi();
	}

	public ContentRepositoryType getType() {
		return ContentRepositoryType.MOCK;
	}
}
