package be.webfactor.c3s.master.service.webserver.domain;

public class WebserverSiteForm {

	private String name;
	private String mailTemplate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMailTemplate() {
		return mailTemplate;
	}

	public void setMailTemplate(String mailTemplate) {
		this.mailTemplate = mailTemplate;
	}
}
