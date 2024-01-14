package be.webfactor.c3s.master.service.webserver.domain;

import lombok.Data;

@Data
public class WebserverSiteContentRepositoryConnection {

	private String type;
	private String repositoryId;
	private String accessToken;
}