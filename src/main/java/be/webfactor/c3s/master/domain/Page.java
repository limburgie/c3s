package be.webfactor.c3s.master.domain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Page {

	private String friendlyUrl;
	private String name;
	private String template;
	private List<Page> children;

	public Page(String name, String template) {
		this(null, name, template);
	}

	public Page(String friendlyUrl, String name, String template) {
		this(friendlyUrl, name, template, Collections.emptyList());
	}

	public Page(String friendlyUrl, String name, String template, List<Page> children) {
		this.friendlyUrl = friendlyUrl;
		this.name = name;
		this.template = template;
		this.children = children;
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

	public List<Page> getChildren() {
		return children;
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
