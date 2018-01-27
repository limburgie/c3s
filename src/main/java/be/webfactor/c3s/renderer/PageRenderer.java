package be.webfactor.c3s.renderer;

import java.util.HashMap;
import java.util.Map;

import be.webfactor.c3s.content.service.ContentService;
import be.webfactor.c3s.master.domain.Page;
import be.webfactor.c3s.master.domain.Template;
import be.webfactor.c3s.master.service.MasterService;
import be.webfactor.c3s.master.templateparser.TemplateParser;

public class PageRenderer {

	private static final String DEFINES_VAR = "defines";

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

		context.put("pages", masterService.getPages());
		context.put("api", contentService.getApi());
		context.put("request", new RequestContext(page, params));

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

		if (!context.containsKey(DEFINES_VAR)) {
			context.put(DEFINES_VAR, new HashMap<String, String>());
		}

		((Map<String, Object>) context.get(DEFINES_VAR)).putAll(parsedDefines);
	}
}