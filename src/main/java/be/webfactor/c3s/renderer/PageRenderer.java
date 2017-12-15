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

	public String render(Page page) {
		String pageContent = renderPageContent(page);

		return renderPage(new RenderContext(pageContent, page));
	}

	private String renderPageContent(Page page) {
		Map<String, Object> pageContext = new HashMap<>();
		pageContext.put("master", masterService);
		pageContext.put("api", contentService.getApi());
		return templateParser.parse(page.getTemplate(), pageContext);
	}

	private String renderPage(RenderContext renderContext) {
		Map<String, Object> siteContext = new HashMap<>();
		siteContext.put("master", masterService);
		siteContext.put("context", renderContext);
		siteContext.put("api", contentService.getApi());

		return templateParser.parse(masterService.getSite().getTemplate(), siteContext);
	}
}
