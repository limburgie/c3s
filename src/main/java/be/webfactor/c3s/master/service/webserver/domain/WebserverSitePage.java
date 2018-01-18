package be.webfactor.c3s.master.service.webserver.domain;

import java.util.ArrayList;
import java.util.List;

public class WebserverSitePage {

	private String friendlyUrl;
	private String name;
	private String templateFile;
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

	public String getTemplateFile() {
		return templateFile;
	}

	public void setTemplateFile(String templateFile) {
		this.templateFile = templateFile;
	}

	public List<WebserverSitePage> getChildren() {
		return children;
	}

	public void setChildren(List<WebserverSitePage> children) {
		this.children = children;
	}
}
