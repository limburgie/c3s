package be.webfactor.c3s.contentrepository.contentful;

import be.webfactor.c3s.contentrepository.ContentRepositoryConnection;
import be.webfactor.c3s.contentrepository.ContentRepositoryType;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.contentful.java.cda.CDAClient;

import be.webfactor.c3s.contentrepository.ContentRepository;
import be.webfactor.c3s.contentrepository.domain.ContentApi;

@Service
@Scope("request")
public class ContentfulContentRepository implements ContentRepository {

	private ContentApi api;

	public void initialize(ContentRepositoryConnection connection) {
		api = new ContentfulContentApi(CDAClient.builder().setSpace(connection.getRepositoryId()).setToken(connection.getAccessToken()).build());
	}

	public ContentApi getApi() {
		return api;
	}

	public ContentRepositoryType getType() {
		return ContentRepositoryType.CONTENTFUL;
	}
}