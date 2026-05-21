package be.webfactor.c3s.siteassetstore.filebased.domain;

import lombok.Data;

@Data
public class SiteMailSettings {

	private String type;
	private String host;
	private int port;
	private String username;
	private String password;
	private String displayName;
	private String fromAddress;
}
