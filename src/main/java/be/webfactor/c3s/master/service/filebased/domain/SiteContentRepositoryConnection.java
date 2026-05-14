package be.webfactor.c3s.master.service.filebased.domain;

import lombok.Data;

@Data
public class SiteContentRepositoryConnection {

	private String type;
	private String repositoryId;
	private String accessToken;
}
