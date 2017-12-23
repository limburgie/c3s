package be.webfactor.c3s.master.domain;

import java.util.Objects;

import be.webfactor.c3s.repository.RepositoryConnection;

public class Site {

	private static final String ERROR_PAGE_FRIENDLY_URL = "error";
	private static final String ERROR_PAGE_NAME = "Error";

	private String name;
	private Page indexPage;
	private String errorTemplate;
	private TemplateEngine templateEngine;
	private String template;
	private RepositoryConnection repositoryConnection;

	public Site(String name, Page indexPage, String errorTemplate, TemplateEngine templateEngine, String template, RepositoryConnection repositoryConnection) {
		this.name = name;
		this.indexPage = indexPage;
		this.errorTemplate = errorTemplate;
		this.templateEngine = templateEngine;
		this.template = template;
		this.repositoryConnection = repositoryConnection;
	}

	public String getName() {
		return name;
	}

	public Page getIndexPage() {
		return indexPage;
	}

	public String getErrorTemplate() {
		return errorTemplate;
	}

	public Page getErrorPage() {
		return new Page(ERROR_PAGE_FRIENDLY_URL, ERROR_PAGE_NAME, errorTemplate);
	}

	public TemplateEngine getTemplateEngine() {
		return templateEngine;
	}

	public String getTemplate() {
		return template;
	}

	public RepositoryConnection getRepositoryConnection() {
		return repositoryConnection;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Site site = (Site) o;
		return Objects.equals(name, site.name) && Objects.equals(indexPage, site.indexPage) && Objects.equals(errorTemplate, this.errorTemplate) && templateEngine == site.templateEngine
				&& Objects.equals(template, site.template) && Objects.equals(repositoryConnection, site.repositoryConnection);
	}

	public int hashCode() {
		return Objects.hash(name, indexPage, errorTemplate, templateEngine, template, repositoryConnection);
	}
}
