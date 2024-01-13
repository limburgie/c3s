package be.webfactor.c3s.master.service.webserver.domain;

import lombok.Data;

@Data
public class WebserverSiteMailSettings {

	private String host;
	private int port;
	private String username;
	private String password;
	private String displayName;
}
