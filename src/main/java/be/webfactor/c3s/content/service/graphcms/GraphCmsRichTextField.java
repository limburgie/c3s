package be.webfactor.c3s.content.service.graphcms;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import be.webfactor.c3s.content.service.domain.AbstractRichTextField;

public class GraphCmsRichTextField extends AbstractRichTextField {

	private String markdownContent;
	private Parser parser = Parser.builder().build();
	private HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();

	GraphCmsRichTextField(String markdownContent) {
		this.markdownContent = markdownContent;
	}

	public String getHtml() {
		return htmlRenderer.render(parser.parse(markdownContent));
	}
}