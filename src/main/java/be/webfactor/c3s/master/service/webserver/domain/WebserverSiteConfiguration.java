package be.webfactor.c3s.master.service.webserver.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WebserverSiteConfiguration {

	private String name;
	private String indexPage;
	private String templateEngine;
	private WebserverSiteContentRepositoryConnection contentRepositoryConnection;
	private WebserverSiteLocationSettings locationSettings;
	private WebserverSiteMailSettings mailSettings;
	private WebserverSitePage errorPage;
	private List<WebserverSiteTemplate> templates;
	private List<WebserverSitePage> pages;
	private List<WebserverSiteForm> forms;

	public List<WebserverSitePage> getAllPages() {
		return getDescendantsOf(pages);
	}

	private List<WebserverSitePage> getDescendantsOf(List<WebserverSitePage> pages) {
		List<WebserverSitePage> results = new ArrayList<>();

		for (WebserverSitePage page : pages) {
			results.add(page);
			results.addAll(getDescendantsOf(page.getChildren()));
		}

		return results;
	}
}