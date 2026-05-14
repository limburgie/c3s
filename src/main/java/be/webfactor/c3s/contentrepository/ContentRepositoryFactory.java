package be.webfactor.c3s.contentrepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

@Service
@Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ContentRepositoryFactory {

	@Autowired private List<ContentRepository> contentRepositories;

	public ContentRepository forConnection(ContentRepositoryConnection connection) {
		ContentRepository contentRepository = getContentRepository(connection);
		contentRepository.initialize(connection);

		return contentRepository;
	}

	private ContentRepository getContentRepository(ContentRepositoryConnection connection) {
		for (ContentRepository contentRepository : contentRepositories) {
			if (connection.getType() == contentRepository.getType()) {
				return contentRepository;
			}
		}

		throw new ContentRepositoryNotFoundException(connection);
	}
}
