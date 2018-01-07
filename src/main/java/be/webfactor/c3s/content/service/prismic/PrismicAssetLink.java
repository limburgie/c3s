package be.webfactor.c3s.content.service.prismic;

import be.webfactor.c3s.content.service.domain.AssetLink;
import io.prismic.Fragment;

public class PrismicAssetLink implements AssetLink {

	private Fragment.Link link;

	PrismicAssetLink(Fragment.Link link) {
		this.link = link;
	}

	public String getUrl() {
		return link == null ? "#" : link.getUrl(null);
	}
}
