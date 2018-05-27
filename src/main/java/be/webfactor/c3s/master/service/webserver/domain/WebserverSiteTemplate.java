package be.webfactor.c3s.master.service.webserver.domain;

import java.util.Map;

public class WebserverSiteTemplate {

	private String name;
	private String contents;
	private String extendsFrom;
	private Map<String, String> inserts;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getExtendsFrom() {
		return extendsFrom;
	}

	public void setExtendsFrom(String extendsFrom) {
		this.extendsFrom = extendsFrom;
	}

	public Map<String, String> getInserts() {
		return inserts;
	}

	public void setInserts(Map<String, String> inserts) {
		this.inserts = inserts;
	}
}
