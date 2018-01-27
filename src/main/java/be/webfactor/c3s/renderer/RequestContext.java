package be.webfactor.c3s.renderer;

import java.util.Map;

import be.webfactor.c3s.master.domain.Page;

public class RequestContext {

	private Page page;
	private String[] params;

	public RequestContext(Page page, String[] params) {
		this.page = page;
		this.params = params;
	}

	public Page getPage() {
		return page;
	}

	public String[] getParams() {
		return params;
	}
}
