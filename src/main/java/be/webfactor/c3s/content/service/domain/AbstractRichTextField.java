package be.webfactor.c3s.content.service.domain;

import org.apache.commons.lang.WordUtils;
import org.jsoup.Jsoup;

public abstract class AbstractRichTextField implements RichTextField {

	public String abbreviate(int length) {
		return WordUtils.abbreviate(Jsoup.parse(getHtml()).text(), length, -1, "...");
	}
}