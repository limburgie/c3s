package be.webfactor.c3s.content.service.mock;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.content.service.ContentService;
import be.webfactor.c3s.content.service.domain.ContentApi;
import be.webfactor.c3s.repository.RepositoryConnection;
import be.webfactor.c3s.repository.RepositoryType;

@Service
@Scope("request")
public class MockContentService implements ContentService {

	public void initialize(RepositoryConnection connection) {

	}

	public ContentApi getApi() {
		return new MockContentApi();
	}

	public RepositoryType getType() {
		return RepositoryType.MOCK;
	}
}
