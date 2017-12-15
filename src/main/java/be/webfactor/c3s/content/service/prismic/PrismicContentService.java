package be.webfactor.c3s.content.service.prismic;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.content.service.ContentService;
import be.webfactor.c3s.repository.RepositoryConnection;
import be.webfactor.c3s.repository.RepositoryType;
import io.prismic.Api;

@Service
@Scope("request")
public class PrismicContentService implements ContentService {

	private Api api;

	public void initialize(RepositoryConnection connection) {
		api = Api.get(connection.getRepositoryId(), connection.getAccessToken());
	}

	public Api getApi() {
		return api;
	}

	public RepositoryType getType() {
		return RepositoryType.PRISMIC;
	}
}
