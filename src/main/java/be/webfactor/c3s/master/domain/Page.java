package be.webfactor.c3s.master.domain;

public class Page {

	private String friendlyUrl;
	private String name;
	private String template;

	public Page(String friendlyUrl, String name, String template) {
		this.friendlyUrl = friendlyUrl;
		this.name = name;
		this.template = template;
	}

	public String getFriendlyUrl() {
		return friendlyUrl;
	}

	public String getName() {
		return name;
	}

	public String getTemplate() {
		return template;
	}
}
