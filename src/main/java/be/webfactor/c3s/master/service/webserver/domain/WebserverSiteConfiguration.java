package be.webfactor.c3s.master.service.webserver.domain;

import java.util.ArrayList;
import java.util.List;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIndexPage() {
		return indexPage;
	}

	public void setIndexPage(String indexPage) {
		this.indexPage = indexPage;
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

	public WebserverSiteLocationSettings getLocationSettings() {
		return locationSettings;
	}

	public void setLocationSettings(WebserverSiteLocationSettings locationSettings) {
		this.locationSettings = locationSettings;
	}

	public WebserverSiteMailSettings getMailSettings() {
		return mailSettings;
	}

	public void setMailSettings(WebserverSiteMailSettings mailSettings) {
		this.mailSettings = mailSettings;
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

	public List<WebserverSiteForm> getForms() {
		return forms;
	}

	public void setForms(List<WebserverSiteForm> forms) {
		this.forms = forms;
	}
}