package be.webfactor.c3s.content.service.contentful;

import java.util.Map;

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

	public String getFilename() {
		return cdaAsset == null ? null : cdaAsset.title();
	}

	public String getExtension() {
		return cdaAsset == null ? null : getUrl().substring(getUrl().lastIndexOf('.') + 1);
	}

	public String getType() {
		return cdaAsset == null ? null : cdaAsset.mimeType();
	}

	public Long getSize() {
		return cdaAsset == null ? null : ((Double) ((Map) ((Map) ((Map) cdaAsset.rawFields().get("file")).values().iterator().next()).get("details")).get("size")).longValue();
	}
}
