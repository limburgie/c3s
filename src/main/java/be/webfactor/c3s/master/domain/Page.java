package be.webfactor.c3s.master.domain;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Page {

	private String friendlyUrl;
	private String name;
	private String contents;
	private Template template;
	private Map<String, String> inserts;
	private List<Page> children;

	/**
	 * Creates a new page that only cares about its URL, name and children (for navigation purposes)
	 */
	public Page(String friendlyUrl, String name, List<Page> children) {
		this(friendlyUrl, name, null, null, null, children);
	}

	/**
	 * Creates a new page that has contents on its own and doesn't depend on a page template, without URL. Children are ignored.
	 */
	public Page(String name, String contents) {
		this(null, name, contents);
	}

	/**
	 * Creates a new page that has contents on its own and doesn't depend on a page template. Children are ignored.
	 */
	public Page(String friendlyUrl, String name, String contents) {
		this(friendlyUrl, name, contents, null, null, Collections.emptyList());
	}

	/**
	 * Creates a new page that extends from a page template, without URL. Children are ignored.
	 */
	public Page(String name, Template template, Map<String, String> inserts) {
		this(null, name, template, inserts);
	}

	/**
	 * Creates a new page that extends from a page template. Children are ignored.
	 */
	public Page(String friendlyUrl, String name, Template template, Map<String, String> inserts) {
		this(friendlyUrl, name, null, template, inserts, Collections.emptyList());
	}

	private Page(String friendlyUrl, String name, String contents, Template template, Map<String, String> inserts, List<Page> children) {
		this.friendlyUrl = friendlyUrl;
		this.name = name;
		this.contents = contents;
		this.template = template;
		this.inserts = inserts;
		this.children = children;
	}

	public boolean isTemplated() {
		return template != null;
	}

	public String getFriendlyUrl() {
		return friendlyUrl;
	}

	public String getName() {
		return name;
	}

	public String getContents() {
		return contents;
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
		return Objects.equals(friendlyUrl, page.friendlyUrl);
	}

	public int hashCode() {
		return Objects.hash(friendlyUrl);
	}
}
