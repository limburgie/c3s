package be.webfactor.c3s.contentrepository.prismic;

import be.webfactor.c3s.contentrepository.ContentRepositoryConnection;
import be.webfactor.c3s.contentrepository.ContentRepositoryType;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.contentrepository.ContentRepository;
import be.webfactor.c3s.contentrepository.domain.ContentApi;
import io.prismic.Api;
import io.prismic.Cache;
import io.prismic.Logger;

@Service
@Scope("request")
public class PrismicContentRepository implements ContentRepository {

	private ContentApi api;

	public void initialize(ContentRepositoryConnection connection) {
		api = new PrismicContentApi(Api.get(connection.getRepositoryId(), connection.getAccessToken(), new Cache.NoCache(), new Logger.NoLogger()));
	}

	public ContentApi getApi() {
		return api;
	}

	public ContentRepositoryType getType() {
		return ContentRepositoryType.PRISMIC;
	}
}