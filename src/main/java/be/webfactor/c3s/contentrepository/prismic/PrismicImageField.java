package be.webfactor.c3s.contentrepository.prismic;

import be.webfactor.c3s.contentrepository.domain.ImageField;
import io.prismic.Fragment;

public class PrismicImageField implements ImageField {

	private Fragment.Image imageFragment;

	PrismicImageField(Fragment.Image imageFragment) {
		this.imageFragment = imageFragment;
	}

	public String getUrl() {
		String baseUrl = getFinalUrl();

		if (baseUrl == null) {
			return null;
		}

		int queryIndex = baseUrl.lastIndexOf('?');

		if (queryIndex != -1) {
			baseUrl = baseUrl.substring(0, queryIndex);
		}

		return baseUrl;
	}

	public String getFinalUrl() {
		return imageFragment.getUrl();
	}

	public String getAlt() {
		String alt = imageFragment.getAlt();

		return alt == null || "null".equals(alt) ? "" : alt;
	}
}