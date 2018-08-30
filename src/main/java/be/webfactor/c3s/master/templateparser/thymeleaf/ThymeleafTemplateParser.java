package be.webfactor.c3s.master.templateparser.thymeleaf;

import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import be.webfactor.c3s.master.domain.TemplateEngine;
import be.webfactor.c3s.master.templateparser.TemplateParser;
import be.webfactor.c3s.master.templateparser.TemplateParserException;

@Service
public class ThymeleafTemplateParser implements TemplateParser {

	private org.thymeleaf.TemplateEngine templateEngine;

	public ThymeleafTemplateParser() {
		templateEngine = new org.thymeleaf.TemplateEngine();
	}

	public String parse(String template, Map<String, Object> context) throws TemplateParserException {
		return templateEngine.process(template, new Context(Locale.getDefault(), context));
	}

	public TemplateEngine getTemplateEngine() {
		return TemplateEngine.THYMELEAF;
	}
}