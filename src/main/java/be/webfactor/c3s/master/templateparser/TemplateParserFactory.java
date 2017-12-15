package be.webfactor.c3s.master.templateparser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.master.domain.TemplateEngine;
import be.webfactor.c3s.master.templateparser.freemarker.FreemarkerTemplateParser;

@Service
public class TemplateParserFactory {

	@Autowired private FreemarkerTemplateParser freemarkerTemplateParser;

	public TemplateParser forTemplateEngine(TemplateEngine templateEngine) {
		switch (templateEngine) {
		case FREEMARKER:
			return freemarkerTemplateParser;
		default:
			throw new TemplateEngineNotSupportedException(templateEngine);
		}
	}
}
