package be.webfactor.c3s.content.service.prismic;

import be.webfactor.c3s.content.service.domain.WebLink;
import io.prismic.Fragment;

public class PrismicWebLink implements WebLink {

	private Fragment.WebLink webLink;

	PrismicWebLink(Fragment.WebLink webLink) {
		this.webLink = webLink;
	}

	public String getUrl() {
		return webLink == null ? "#" : webLink.getUrl();
	}

	public boolean isEmpty() {
		return webLink == null;
	}
}
