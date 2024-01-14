package be.webfactor.c3s.master.service.webserver.domain;

import lombok.Data;

@Data
public class WebserverSiteEmail {

	private String subject;
	private String contents;
}
