package be.webfactor.c3s.master.service.webserver.domain;

import lombok.Data;

@Data
public class WebserverSiteForm {

	private String name;
	private WebserverSiteEmail visitorEmail;
	private WebserverSiteEmail managerEmail;
}
