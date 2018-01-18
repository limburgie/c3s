package be.webfactor.c3s.renderer;

import java.util.HashMap;
import java.util.Map;

import be.webfactor.c3s.content.service.ContentService;
import be.webfactor.c3s.master.domain.Page;
import be.webfactor.c3s.master.service.MasterService;
import be.webfactor.c3s.master.templateparser.TemplateParser;

public class PageRenderer {

	private MasterService masterService;
	private TemplateParser templateParser;
	private ContentService contentService;

	PageRenderer(MasterService masterService, ContentService contentService, TemplateParser templateParser) {
		this.masterService = masterService;
		this.contentService = contentService;
		this.templateParser = templateParser;
	}

	public String render(Page page, String[] params) {
		String body = renderPageContent(page, params);

		return renderPage(body, page);
	}

	private String renderPageContent(Page page, String[] params) {
		Map<String, Object> pageContext = new HashMap<>();

		setGeneralContext(page, pageContext);
		pageContext.put("params", params);

		return templateParser.parse(page.getTemplate(), pageContext);
	}

	private String renderPage(String body, Page page) {
		Map<String, Object> siteContext = new HashMap<>();

		setGeneralContext(page, siteContext);
		siteContext.put("body", body);

		return templateParser.parse(masterService.getSiteTemplate(), siteContext);
	}

	private void setGeneralContext(Page page, Map<String, Object> context) {
		context.put("pages", masterService.getPages());
		context.put("page", page);
		context.put("api", contentService.getApi());
	}
}
