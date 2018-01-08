package be.webfactor.c3s.content.service.prismic;

import org.apache.commons.lang.WordUtils;
import org.jsoup.Jsoup;

import be.webfactor.c3s.content.service.domain.RichTextField;
import io.prismic.Fragment;

public class PrismicRichTextField implements RichTextField {

	private Fragment.StructuredText structuredText;

	PrismicRichTextField(Fragment.StructuredText structuredText) {
		this.structuredText = structuredText;
	}

	public String getHtml() {
		return structuredText == null ? "" : structuredText.asHtml(null);
	}

	public String abbreviate(int length) {
		return WordUtils.abbreviate(Jsoup.parse(getHtml()).text(), length, -1, "...");
	}
}
