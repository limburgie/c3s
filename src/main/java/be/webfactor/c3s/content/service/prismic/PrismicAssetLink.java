package be.webfactor.c3s.content.service.prismic;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import be.webfactor.c3s.content.service.domain.AssetLink;
import io.prismic.Fragment;

public class PrismicAssetLink implements AssetLink {

	private Fragment.Link link;

	PrismicAssetLink(Fragment.Link link) {
		this.link = link;
	}

	public String getUrl() {
		return link.getUrl(null);
	}

	public String getFilename() {
		if (link instanceof Fragment.FileLink) {
			return ((Fragment.FileLink) link).getFilename();
		}

		try {
			String url = URLDecoder.decode(getUrl(), StandardCharsets.UTF_8.name());
			url = url.substring(url.lastIndexOf('/') + 1);

			return url.substring(url.indexOf('_') + 1) + "." + getExtension();
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public String getExtension() {
		return getUrl().substring(getUrl().lastIndexOf('.') + 1);
	}

	public String getType() {
		if (!(link instanceof Fragment.FileLink)) {
			return null;
		}

		return ((Fragment.FileLink) link).getKind();
	}

	public Long getSize() {
		if (!(link instanceof Fragment.FileLink)) {
			return null;
		}

		return ((Fragment.FileLink) link).getSize();
	}
}