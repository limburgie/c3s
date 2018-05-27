package be.webfactor.c3s.master.service;

import be.webfactor.c3s.repository.RepositoryConnection;

class MasterServiceNotFoundException extends RuntimeException {

	MasterServiceNotFoundException(RepositoryConnection connection) {
		super(String.format("No master repository found for connection %s", connection));
	}
}
