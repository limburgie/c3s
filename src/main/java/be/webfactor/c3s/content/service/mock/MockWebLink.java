package be.webfactor.c3s.content.service.mock;

import be.webfactor.c3s.content.service.domain.WebLink;

public class MockWebLink implements WebLink {

	public String getUrl() {
		return "https://www.google.com";
	}

	public boolean isEmpty() {
		return false;
	}
}
