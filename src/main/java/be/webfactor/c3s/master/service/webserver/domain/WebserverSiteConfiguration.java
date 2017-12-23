package be.webfactor.c3s.master.service.webserver.domain;

import java.util.List;

public class WebserverSiteConfiguration {

	private String name;
	private String indexPage;
	private String errorTemplateFile;
	private String templateEngine;
	private String templateFile;
	private WebserverSiteContentRepositoryConnection contentRepositoryConnection;

	private List<WebserverSitePage> pages;

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

	public String getErrorTemplateFile() {
		return errorTemplateFile;
	}

	public void setErrorTemplateFile(String errorTemplateFile) {
		this.errorTemplateFile = errorTemplateFile;
	}

	public String getTemplateEngine() {
		return templateEngine;
	}

	public void setTemplateEngine(String templateEngine) {
		this.templateEngine = templateEngine;
	}

	public String getTemplateFile() {
		return templateFile;
	}

	public void setTemplateFile(String templateFile) {
		this.templateFile = templateFile;
	}

	public WebserverSiteContentRepositoryConnection getContentRepositoryConnection() {
		return contentRepositoryConnection;
	}

	public void setContentRepositoryConnection(WebserverSiteContentRepositoryConnection contentRepositoryConnection) {
		this.contentRepositoryConnection = contentRepositoryConnection;
	}

	public List<WebserverSitePage> getPages() {
		return pages;
	}

	public void setPages(List<WebserverSitePage> pages) {
		this.pages = pages;
	}
}
