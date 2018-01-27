package be.webfactor.c3s.master.service.webserver.domain;

import java.util.ArrayList;
import java.util.List;

import be.webfactor.c3s.master.domain.Page;

public class WebserverSiteConfiguration {

	private String name;
	private String indexPageFriendlyUrl;
	private String templateEngine;
	private WebserverSiteContentRepositoryConnection contentRepositoryConnection;
	private WebserverSitePage errorPage;
	private List<WebserverSiteTemplate> templates;
	private List<WebserverSitePage> pages;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIndexPageFriendlyUrl() {
		return indexPageFriendlyUrl;
	}

	public void setIndexPageFriendlyUrl(String indexPageFriendlyUrl) {
		this.indexPageFriendlyUrl = indexPageFriendlyUrl;
	}

	public String getTemplateEngine() {
		return templateEngine;
	}

	public void setTemplateEngine(String templateEngine) {
		this.templateEngine = templateEngine;
	}

	public WebserverSiteContentRepositoryConnection getContentRepositoryConnection() {
		return contentRepositoryConnection;
	}

	public void setContentRepositoryConnection(WebserverSiteContentRepositoryConnection contentRepositoryConnection) {
		this.contentRepositoryConnection = contentRepositoryConnection;
	}

	public WebserverSitePage getErrorPage() {
		return errorPage;
	}

	public void setErrorPage(WebserverSitePage errorPage) {
		this.errorPage = errorPage;
	}

	public List<WebserverSiteTemplate> getTemplates() {
		return templates;
	}

	public void setTemplates(List<WebserverSiteTemplate> templates) {
		this.templates = templates;
	}

	public List<WebserverSitePage> getPages() {
		return pages;
	}

	public void setPages(List<WebserverSitePage> pages) {
		this.pages = pages;
	}
}
