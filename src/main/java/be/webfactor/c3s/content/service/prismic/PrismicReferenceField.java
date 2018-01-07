package be.webfactor.c3s.content.service.prismic;

import be.webfactor.c3s.content.service.domain.ContentItem;
import be.webfactor.c3s.content.service.domain.ReferenceField;
import io.prismic.Api;
import io.prismic.Fragment;

public class PrismicReferenceField implements ReferenceField {

	private Fragment.DocumentLink link;
	private Api api;

	PrismicReferenceField(Fragment.Link link, Api api) {
		this.link = (Fragment.DocumentLink) link;
		this.api = api;
	}

	public ContentItem getItem() {
		return new PrismicContentItem(api.getByID(link.getId()), api);
	}
}
