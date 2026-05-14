package be.webfactor.c3s.master.service.filebased.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SiteConfiguration {

	private String name;
	private String indexPage;
	private String templateEngine;
	private SiteContentRepositoryConnection contentRepositoryConnection;
	private SiteLocationSettings locationSettings;
	private SiteMailSettings mailSettings;
	private SitePage errorPage;
	private List<SiteTemplate> templates;
	private List<SitePage> pages;
	private List<SiteForm> forms;

	public List<SitePage> getAllPages() {
		return getDescendantsOf(pages);
	}

	private List<SitePage> getDescendantsOf(List<SitePage> pages) {
		List<SitePage> results = new ArrayList<>();

		for (SitePage page : pages) {
			results.add(page);
			results.addAll(getDescendantsOf(page.getChildren()));
		}

		return results;
	}
}
