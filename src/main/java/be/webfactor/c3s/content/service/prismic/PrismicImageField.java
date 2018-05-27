package be.webfactor.c3s.content.service.prismic;

import be.webfactor.c3s.content.service.domain.ImageField;
import io.prismic.Fragment;

public class PrismicImageField implements ImageField {

	private Fragment.Image imageFragment;

	PrismicImageField(Fragment.Image imageFragment) {
		this.imageFragment = imageFragment;
	}

	public String getUrl() {
		return imageFragment == null ? "#" : imageFragment.getUrl();
	}

	public String getAlt() {
		String alt = imageFragment == null ? null : imageFragment.getAlt();

		return alt == null || "null".equals(alt) ? "" : alt;
	}

	public boolean isEmpty() {
		return imageFragment == null;
	}
}
