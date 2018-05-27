package be.webfactor.c3s.content.service;

import be.webfactor.c3s.content.service.domain.ContentApi;
import be.webfactor.c3s.repository.RepositoryConnection;
import be.webfactor.c3s.repository.RepositoryType;

/**
 * The content service is an abstraction for a backend that stores structured content and provides an API to query this content.
 * The content service is initialized using a repository connection.
 */
public interface ContentService {

	/**
	 * Initializes this content service with the given repository connection.
	 * The content service can use this connection to bootstrap its configuration.
	 */
	void initialize(RepositoryConnection connection);

	/**
	 * Returns the back-end agnostic content API used for querying content from the repository.
	 */
	ContentApi getApi();

	/**
	 * Returns the type of this repository.
	 */
	RepositoryType getType();
}
