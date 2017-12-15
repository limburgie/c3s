package be.webfactor.c3s.renderer;

import be.webfactor.c3s.master.domain.Page;

public class RenderContext {

	private String body;
	private Page currentPage;

	RenderContext(String body, Page currentPage) {
		this.body = body;
		this.currentPage = currentPage;
	}

	public String getBody() {
		return body;
	}

	public Page getCurrentPage() {
		return currentPage;
	}
}
