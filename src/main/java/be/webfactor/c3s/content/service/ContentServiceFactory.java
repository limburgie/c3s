package be.webfactor.c3s.content.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.repository.RepositoryConnection;

@Service
@Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ContentServiceFactory {

	@Autowired private List<ContentService> contentServices;

	public ContentService forRepositoryConnection(RepositoryConnection connection) {
		ContentService contentService = getContentService(connection);
		contentService.initialize(connection);

		return contentService;
	}

	private ContentService getContentService(RepositoryConnection connection) {
		for (ContentService contentService : contentServices) {
			if (connection.getType() == contentService.getType()) {
				return contentService;
			}
		}

		throw new ContentServiceNotFoundException(connection);
	}
}
