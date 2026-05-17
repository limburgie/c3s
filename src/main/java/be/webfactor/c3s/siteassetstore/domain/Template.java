package be.webfactor.c3s.siteassetstore.domain;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Template {

	private final String name;
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
}
