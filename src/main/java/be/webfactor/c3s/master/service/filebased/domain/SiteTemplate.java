package be.webfactor.c3s.master.service.filebased.domain;

import lombok.Data;

import java.util.Map;

@Data
public class SiteTemplate {

	private String name;
	private String contents;
	private String extendsFrom;
	private Map<String, String> inserts;
}
