package be.webfactor.c3s.master.service.webserver.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class WebserverSitePage {

	private String friendlyUrl;
	private String name;
	private boolean hidden;
	private String contents;
	private String template;
	private Map<String, String> inserts = new HashMap<>();
	private List<WebserverSitePage> children = new ArrayList<>();

	public boolean isTemplated() {
		return template != null;
	}
}