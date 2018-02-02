package be.webfactor.c3s.content.service.mock;

import org.apache.commons.lang.WordUtils;
import org.jsoup.Jsoup;

import be.webfactor.c3s.content.service.domain.RichTextField;

public class MockRichTextField implements RichTextField {

	private String value;

	MockRichTextField(String value) {
		this.value = value;
	}

	public String getHtml() {
		return "<p>" + value + ". " + MockRandomGenerator.alinea() + "</p><p>" + MockRandomGenerator.alinea() + "</p>";
	}

	public String abbreviate(int length) {
		return WordUtils.abbreviate(Jsoup.parse(getHtml()).text(), length, -1, "...");
	}

	public boolean isEmpty() {
		return false;
	}
}
