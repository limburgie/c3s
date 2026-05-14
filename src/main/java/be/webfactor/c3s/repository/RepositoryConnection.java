package be.webfactor.c3s.repository;

import lombok.Value;

@Value
public class RepositoryConnection {

	RepositoryType type;
	String repositoryId;
	String accessToken;
	String secretKey;
	String region;

	public static RepositoryConnection of(RepositoryType type, String repositoryId, String accessToken) {
		return new RepositoryConnection(type, repositoryId, accessToken, null, null);
	}
}
