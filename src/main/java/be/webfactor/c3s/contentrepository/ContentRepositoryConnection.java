package be.webfactor.c3s.contentrepository;

import lombok.Value;

@Value
public class ContentRepositoryConnection {

	ContentRepositoryType type;
	String repositoryId;
	String accessToken;

	public static ContentRepositoryConnection of(ContentRepositoryType type, String repositoryId, String accessToken) {
		return new ContentRepositoryConnection(type, repositoryId, accessToken);
	}
}
