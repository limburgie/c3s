package be.webfactor.c3s.siteassetstore.filebased.domain;

import lombok.Data;

@Data
public class SiteContentRepositoryConnection {

	private String type;
	private String repositoryId;
	private String accessToken;
}
