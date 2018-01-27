package be.webfactor.c3s.master.domain;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Page {

	private String friendlyUrl;
	private String name;
	private Template template;
	private Map<String, String> inserts;
	private List<Page> children;

	public Page(String name, Template template, Map<String, String> inserts) {
		this(null, name, template, inserts, Collections.emptyList());
	}

	public Page(String friendlyUrl, String name, Template template, Map<String, String> inserts, List<Page> children) {
		this.friendlyUrl = friendlyUrl;
		this.name = name;
		this.template = template;
		this.inserts = inserts;
		this.children = children;
	}

	public String getFriendlyUrl() {
		return friendlyUrl;
	}

	public String getName() {
		return name;
	}

	public Template getTemplate() {
		return template;
	}

	public Map<String, String> getInserts() {
		return inserts;
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
