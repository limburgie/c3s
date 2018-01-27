package be.webfactor.c3s.renderer;

import java.util.HashMap;
import java.util.Map;

import be.webfactor.c3s.content.service.ContentService;
import be.webfactor.c3s.master.domain.Page;
import be.webfactor.c3s.master.domain.Template;
import be.webfactor.c3s.master.service.MasterService;
import be.webfactor.c3s.master.templateparser.TemplateParser;

public class PageRenderer {

	private static final String API_TEMPLATE_VAR = "api";
	private static final String SITE_TEMPLATE_VAR = "site";
	private static final String REQUEST_TEMPLATE_VAR = "request";
	private static final String DEFINES_TEMPLATE_VAR = "defines";

	private MasterService masterService;
	private TemplateParser templateParser;
	private ContentService contentService;

	PageRenderer(MasterService masterService, ContentService contentService, TemplateParser templateParser) {
		this.masterService = masterService;
		this.contentService = contentService;
		this.templateParser = templateParser;
	}

	public String render(Page page, String[] params) {
		Map<String, Object> context = new HashMap<>();

		context.put(API_TEMPLATE_VAR, contentService.getApi());
		context.put(SITE_TEMPLATE_VAR, new SiteContext(masterService.getSiteName(), masterService.getPages()));
		context.put(REQUEST_TEMPLATE_VAR, new RequestContext(page, params));

		addParsedDefinesToContext(page.getDefines(), context);

		return renderTemplate(page.getTemplate(), context);
	}

	private String renderTemplate(Template template, Map<String, Object> context) {
		if (template.getContents() != null) {
			return templateParser.parse(template.getContents(), context);
		}

		addParsedDefinesToContext(template.getDefines(), context);

		return renderTemplate(template.getExtendedTemplate(), context);
	}

	private void addParsedDefinesToContext(Map<String, String> defines, Map<String, Object> context) {
		Map<String, String> parsedDefines = new HashMap<>();

		defines.forEach((key, value) -> parsedDefines.put(key, templateParser.parse(value, context)));

		if (!context.containsKey(DEFINES_TEMPLATE_VAR)) {
			context.put(DEFINES_TEMPLATE_VAR, new HashMap<String, String>());
		}

		((Map<String, Object>) context.get(DEFINES_TEMPLATE_VAR)).putAll(parsedDefines);
	}
}