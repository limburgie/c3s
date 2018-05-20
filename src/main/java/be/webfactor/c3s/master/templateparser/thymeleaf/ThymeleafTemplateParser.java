package be.webfactor.c3s.master.templateparser.thymeleaf;

import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.TemplateResolver;

import be.webfactor.c3s.master.domain.TemplateEngine;
import be.webfactor.c3s.master.templateparser.TemplateParser;
import be.webfactor.c3s.master.templateparser.TemplateParserException;

@Service
public class ThymeleafTemplateParser implements TemplateParser {

	private org.thymeleaf.TemplateEngine templateEngine;

	public ThymeleafTemplateParser() {
		templateEngine = new org.thymeleaf.TemplateEngine();
		TemplateResolver templateResolver = new StringBasedTemplateResolver();
		templateResolver.setTemplateMode("HTML5");
		templateEngine.setTemplateResolver(templateResolver);
	}

	public String parse(String template, Map<String, Object> context) throws TemplateParserException {
		Context tlContext = new Context(Locale.getDefault(), context);
		StringWriter stringWriter = new StringWriter();
		templateEngine.process(template, tlContext, stringWriter);

		return stringWriter.toString();
	}

	public TemplateEngine getTemplateEngine() {
		return TemplateEngine.THYMELEAF;
	}
}