package be.webfactor.c3s.master.service.webserver.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebserverSitePage {

	private String friendlyUrl;
	private String name;
	private String template;
	private Map<String, String> defines = new HashMap<>();
	private List<WebserverSitePage> children = new ArrayList<>();

	public String getFriendlyUrl() {
		return friendlyUrl;
	}

	public void setFriendlyUrl(String friendlyUrl) {
		this.friendlyUrl = friendlyUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplateFile(String template) {
		this.template = template;
	}

	public Map<String, String> getDefines() {
		return defines;
	}

	public void setDefines(Map<String, String> defines) {
		this.defines = defines;
	}

	public List<WebserverSitePage> getChildren() {
		return children;
	}

	public void setChildren(List<WebserverSitePage> children) {
		this.children = children;
	}
}
