package be.webfactor.c3s.renderer;

import java.util.List;

import be.webfactor.c3s.master.domain.Page;

public class SiteContext {

	private String name;
	private List<Page> pages;

	public SiteContext(String name, List<Page> pages) {
		this.name = name;
		this.pages = pages;
	}

	public String getName() {
		return name;
	}

	public List<Page> getPages() {
		return pages;
	}
}
