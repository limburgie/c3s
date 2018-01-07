package be.webfactor.c3s.content.service.prismic;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

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

	public String getFilename() {
		if (link == null) {
			return null;
		}

		if (link instanceof Fragment.FileLink) {
			String fileNameIncludingExtension = ((Fragment.FileLink) link).getFilename();
			return fileNameIncludingExtension.substring(0, fileNameIncludingExtension.lastIndexOf('.'));
		}

		try {
			String url = URLDecoder.decode(getUrl(), "UTF-8");
			url = url.substring(url.lastIndexOf('/') + 1);

			return url.substring(url.indexOf('_') + 1);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public String getExtension() {
		return link == null ? null : getUrl().substring(getUrl().lastIndexOf('.') + 1);
	}

	public String getType() {
		if (link == null || !(link instanceof Fragment.FileLink)) {
			return null;
		}

		return ((Fragment.FileLink) link).getKind();
	}

	public Long getSize() {
		if (link == null || !(link instanceof Fragment.FileLink)) {
			return null;
		}

		return ((Fragment.FileLink) link).getSize();
	}
}
