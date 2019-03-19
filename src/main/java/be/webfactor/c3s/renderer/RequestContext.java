package be.webfactor.c3s.renderer;

import java.util.Locale;

import be.webfactor.c3s.master.domain.Page;

public class RequestContext {

	private Page page;
	private String[] params;
	private Locale locale;

	RequestContext(Page page, String[] params, Locale locale) {
		this.page = page;
		this.params = params;
		this.locale = locale;
	}

	public Page getPage() {
		return page;
	}

	public String[] getParams() {
		return params;
	}

	public Locale getLocale() {
		return locale;
	}
}
