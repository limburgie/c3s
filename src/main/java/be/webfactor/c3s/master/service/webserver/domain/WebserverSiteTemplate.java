package be.webfactor.c3s.master.service.webserver.domain;

import lombok.Data;

import java.util.Map;

@Data
public class WebserverSiteTemplate {

	private String name;
	private String contents;
	private String extendsFrom;
	private Map<String, String> inserts;
}