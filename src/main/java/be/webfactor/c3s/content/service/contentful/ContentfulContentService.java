package be.webfactor.c3s.content.service.contentful;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.contentful.java.cda.CDAClient;

import be.webfactor.c3s.content.service.ContentService;
import be.webfactor.c3s.content.service.domain.ContentApi;
import be.webfactor.c3s.repository.RepositoryConnection;
import be.webfactor.c3s.repository.RepositoryType;

@Service
@Scope("request")
public class ContentfulContentService implements ContentService {

	private ContentApi api;

	public void initialize(RepositoryConnection connection) {
		api = new ContentfulContentApi(CDAClient.builder().setSpace(connection.getRepositoryId()).setToken(connection.getAccessToken()).build());
	}

	public ContentApi getApi() {
		return api;
	}

	public RepositoryType getType() {
		return RepositoryType.CONTENTFUL;
	}
}