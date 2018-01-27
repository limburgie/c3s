package be.webfactor.c3s.master.domain;

import java.util.HashMap;
import java.util.Map;

public class Template {

	private String name;
	private String contents;
	private Template extendedTemplate;
	private Map<String, String> inserts = new HashMap<>();

	public Template(String name, String contents) {
		this.name = name;
		this.contents = contents;
	}

	public Template(String name, Template extendedTemplate, Map<String, String> inserts) {
		this.name = name;
		this.extendedTemplate = extendedTemplate;
		this.inserts = inserts;
	}

	public String getName() {
		return name;
	}

	public String getContents() {
		return contents;
	}

	public Template getExtendedTemplate() {
		return extendedTemplate;
	}

	public Map<String, String> getInserts() {
		return inserts;
	}
}
