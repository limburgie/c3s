package be.webfactor.c3s.content.service;

import be.webfactor.c3s.repository.RepositoryConnection;

class ContentServiceNotFoundException extends RuntimeException {

	ContentServiceNotFoundException(RepositoryConnection connection) {
		super(String.format("No master repository found for connection %s", connection));
	}
}
