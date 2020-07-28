package be.webfactor.c3s.master.service.webserver.domain;

public class WebserverSiteEmail {

	private String subject;
	private String contents;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}
}
