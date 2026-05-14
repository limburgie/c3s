package be.webfactor.c3s.master.templateparser;

import java.util.Map;

import be.webfactor.c3s.master.domain.TemplateEngine;
import be.webfactor.c3s.master.service.MasterService;

/**
 * The template parser is an abstract parser that converts a template to String based content, given an object context.
 */
public interface TemplateParser {

	/**
	 * Parses the given template with the given name and contents to a String based on the provided object context.
	 * The master service is also provided so templates that include other templates can resolve them via the master service.
	 */
	String parse(String templateName, String templateContents, Map<String, Object> context, MasterService masterService) throws TemplateParserException;

	/**
	 * Returns the template engine for which this template parser is a suitable parser.
	 */
	TemplateEngine getTemplateEngine();
}
