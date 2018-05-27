package be.webfactor.c3s.repository;

public class RepositoryConnection {

	private RepositoryType type;
	private String repositoryId;
	private String accessToken;

	public RepositoryConnection(RepositoryType type, String repositoryId, String accessToken) {
		this.type = type;
		this.repositoryId = repositoryId;
		this.accessToken = accessToken;
	}

	public RepositoryType getType() {
		return type;
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	public String getAccessToken() {
		return accessToken;
	}
}
