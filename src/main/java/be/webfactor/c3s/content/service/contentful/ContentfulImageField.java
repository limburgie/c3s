package be.webfactor.c3s.content.service.contentful;

import com.contentful.java.cda.CDAAsset;

import be.webfactor.c3s.content.service.domain.ImageField;

public class ContentfulImageField implements ImageField {

	private CDAAsset cdaAsset;

	ContentfulImageField(CDAAsset cdaAsset) {
		this.cdaAsset = cdaAsset;
	}

	public String getUrl() {
		return cdaAsset.url();
	}

	public String getFinalUrl() {
		return getUrl();
	}

	public String getAlt() {
		return cdaAsset.title();
	}
}