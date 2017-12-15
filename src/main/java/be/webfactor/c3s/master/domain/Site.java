package be.webfactor.c3s.master.domain;

import be.webfactor.c3s.repository.RepositoryConnection;

public class Site {

	private String name;
	private Page indexPage;
	private TemplateEngine templateEngine;
	private String template;
	private RepositoryConnection repositoryConnection;

	public Site(String name, Page indexPage, TemplateEngine templateEngine, String template, RepositoryConnection repositoryConnection) {
		this.name = name;
		this.indexPage = indexPage;
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

	public TemplateEngine getTemplateEngine() {
		return templateEngine;
	}

	public String getTemplate() {
		return template;
	}

	public RepositoryConnection getRepositoryConnection() {
		return repositoryConnection;
	}
}
