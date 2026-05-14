package be.webfactor.c3s.contentrepository;

class ContentRepositoryNotFoundException extends RuntimeException {

	ContentRepositoryNotFoundException(ContentRepositoryConnection connection) {
		super(String.format("No content repository found for connection %s", connection));
	}
}
