package be.webfactor.c3s.contentrepository;

import be.webfactor.c3s.contentrepository.domain.ContentApi;

/**
 * The content repository is an abstraction for a backend that stores structured content and provides an API to query this content.
 * The content repository is initialized using a repository connection.
 */
public interface ContentRepository {

	/**
	 * Initializes this content repository with the given repository connection.
	 * The content repository can use this connection to bootstrap its configuration.
	 */
	void initialize(ContentRepositoryConnection connection);

	/**
	 * Returns the back-end agnostic content API used for querying content from the repository.
	 */
	ContentApi getApi();

	/**
	 * Returns the type of this repository.
	 */
	ContentRepositoryType getType();
}
