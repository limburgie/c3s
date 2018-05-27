package be.webfactor.c3s.master.templateparser;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.master.domain.TemplateEngine;

@Service
public class TemplateParserFactory {

	@Autowired private List<TemplateParser> templateParsers;

	public TemplateParser forTemplateEngine(TemplateEngine templateEngine) {
		for (TemplateParser templateParser : templateParsers) {
			if (templateEngine == templateParser.getTemplateEngine()) {
				return templateParser;
			}
		}

		throw new TemplateEngineNotSupportedException(templateEngine);
	}
}
