package be.webfactor.c3s.master.service.webserver.domain;

public class WebserverSiteForm {

	private String name;
	private WebserverSiteEmail visitorEmail;
	private WebserverSiteEmail managerEmail;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public WebserverSiteEmail getVisitorEmail() {
		return visitorEmail;
	}

	public void setVisitorEmail(WebserverSiteEmail visitorEmail) {
		this.visitorEmail = visitorEmail;
	}

	public WebserverSiteEmail getManagerEmail() {
		return managerEmail;
	}

	public void setManagerEmail(WebserverSiteEmail managerEmail) {
		this.managerEmail = managerEmail;
	}
}
