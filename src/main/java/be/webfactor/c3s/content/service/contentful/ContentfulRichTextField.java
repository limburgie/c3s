package be.webfactor.c3s.content.service.contentful;

import org.apache.commons.lang.WordUtils;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;

import be.webfactor.c3s.content.service.domain.RichTextField;

public class ContentfulRichTextField implements RichTextField {

	private String markdownContent;
	private Parser parser = Parser.builder().build();
	private HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();

	ContentfulRichTextField(String markdownContent) {
		this.markdownContent = markdownContent;
	}

	public String getHtml() {
		return htmlRenderer.render(parser.parse(markdownContent));
	}

	public String abbreviate(int length) {
		return WordUtils.abbreviate(Jsoup.parse(getHtml()).text(), length, -1, "...");
	}
}
