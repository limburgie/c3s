package be.webfactor.c3s.repository;

import lombok.Value;

@Value
public class RepositoryConnection {

	RepositoryType type;
	String repositoryId;
	String accessToken;
}
