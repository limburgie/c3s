package be.webfactor.c3s.content.service.domain;

import org.apache.commons.text.WordUtils;
import org.jsoup.Jsoup;

public abstract class RichTextField {

	public abstract String getHtml();

	public String abbreviate(int length) {
		return WordUtils.abbreviate(Jsoup.parse(getHtml()).text(), length, -1, "...");
	}
}