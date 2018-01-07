package be.webfactor.c3s.content.service.contentful;

import com.contentful.java.cda.CDAAsset;

import be.webfactor.c3s.content.service.domain.AssetLink;

public class ContentfulAssetLink implements AssetLink {

	private CDAAsset cdaAsset;

	ContentfulAssetLink(CDAAsset cdaAsset) {
		this.cdaAsset = cdaAsset;
	}

	public String getUrl() {
		return cdaAsset == null ? "#" : cdaAsset.url();
	}
}
