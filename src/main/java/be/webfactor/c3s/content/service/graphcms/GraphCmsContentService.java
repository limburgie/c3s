package be.webfactor.c3s.content.service.graphcms;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.content.service.ContentService;
import be.webfactor.c3s.content.service.domain.ContentApi;
import be.webfactor.c3s.repository.RepositoryConnection;
import be.webfactor.c3s.repository.RepositoryType;

@Service
@Scope("request")
public class GraphCmsContentService implements ContentService {

	private ContentApi api;

	public void initialize(RepositoryConnection connection) {
		api = new GraphCmsContentApi(new GraphCmsClient(connection));
	}

	public ContentApi getApi() {
		return api;
	}

	public RepositoryType getType() {
		return RepositoryType.GRAPHCMS;
	}
}