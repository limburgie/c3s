package be.webfactor.c3s.content.service;

import be.webfactor.c3s.content.service.domain.ContentApi;
import be.webfactor.c3s.repository.RepositoryConnection;
import be.webfactor.c3s.repository.RepositoryType;

public interface ContentService {

	void initialize(RepositoryConnection connection);

	ContentApi getApi();

	RepositoryType getType();
}
