package be.webfactor.c3s.master.domain;

import java.util.Objects;

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

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Page page = (Page) o;
		return Objects.equals(friendlyUrl, page.friendlyUrl) && Objects.equals(name, page.name) && Objects.equals(template, page.template);
	}

	public int hashCode() {

		return Objects.hash(friendlyUrl, name, template);
	}
}
