package be.webfactor.c3s.renderer;

import java.util.HashMap;
import java.util.Map;

import be.webfactor.c3s.shopping.ShoppingCartThreadLocal;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import be.webfactor.c3s.content.service.ContentService;
import be.webfactor.c3s.controller.PageController;
import be.webfactor.c3s.master.domain.LocationThreadLocal;
import be.webfactor.c3s.master.domain.Page;
import be.webfactor.c3s.master.domain.Template;
import be.webfactor.c3s.master.service.MasterService;
import be.webfactor.c3s.master.templateparser.TemplateParser;

public class PageRenderer {

	private static final String API_TEMPLATE_VAR = "api";
	private static final String SITE_TEMPLATE_VAR = "site";
	private static final String REQUEST_TEMPLATE_VAR = "request";
	private static final String INSERTS_TEMPLATE_VAR = "inserts";
	private static final String I18N_TEMPLATE_VAR = "i18n";
	private static final String SHOPPING_CART_VAR = "cart";

	private MasterService masterService;
	private TemplateParser templateParser;
	private ContentService contentService;

	PageRenderer(MasterService masterService, ContentService contentService, TemplateParser templateParser) {
		this.masterService = masterService;
		this.contentService = contentService;
		this.templateParser = templateParser;
	}

	public String render(Page page, String[] params) {
		String result = doRender(page, params);

		Document doc = Jsoup.parse(result);
		doc.body().append(String.format("<script src=\"%s\"></script>", PageController.EDIT_URL_JS_PATH));

		return doc.html();
	}

	private String doRender(Page page, String[] params) {
		Map<String, Object> context = new HashMap<>();

		context.put(API_TEMPLATE_VAR, contentService == null ? null : contentService.getApi());
		context.put(SITE_TEMPLATE_VAR, new SiteContext(masterService.getSiteName(), masterService.getPages()));
		context.put(REQUEST_TEMPLATE_VAR, new RequestContext(page, params, LocationThreadLocal.getLocale()));
		context.put(I18N_TEMPLATE_VAR, new I18n(masterService.getResourceBundle()));
		context.put(SHOPPING_CART_VAR, ShoppingCartThreadLocal.getShoppingCart());

		if (page.isTemplated()) {
			addParsedInsertsToContext(page.getName(), page.getInserts(), context);

			return renderTemplate(page.getTemplate(), context);
		}

		return templateParser.parse(page.getName(), page.getContents(), context, masterService.getBaseUrl());
	}

	private String renderTemplate(Template template, Map<String, Object> context) {
		if (template.getContents() != null) {
			return templateParser.parse(template.getName(), template.getContents(), context, masterService.getBaseUrl());
		}

		addParsedInsertsToContext(template.getName(), template.getInserts(), context);

		return renderTemplate(template.getExtendedTemplate(), context);
	}

	private void addParsedInsertsToContext(String templateName, Map<String, String> inserts, Map<String, Object> context) {
		Map<String, String> parsedInserts = new HashMap<>();

		inserts.forEach((key, value) -> parsedInserts.put(key, templateParser.parse(templateName, value, context, masterService.getBaseUrl())));

		if (!context.containsKey(INSERTS_TEMPLATE_VAR)) {
			context.put(INSERTS_TEMPLATE_VAR, new HashMap<String, String>());
		}

		((Map<String, Object>) context.get(INSERTS_TEMPLATE_VAR)).putAll(parsedInserts);
	}
}