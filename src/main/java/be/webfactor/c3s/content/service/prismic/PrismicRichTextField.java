package be.webfactor.c3s.content.service.prismic;

import be.webfactor.c3s.content.service.domain.RichTextField;
import io.prismic.Fragment;

public class PrismicRichTextField extends RichTextField {

	private Fragment.StructuredText structuredText;

	PrismicRichTextField(Fragment.StructuredText structuredText) {
		this.structuredText = structuredText;
	}

	public String getHtml() {
		return structuredText.asHtml(null);
	}
}