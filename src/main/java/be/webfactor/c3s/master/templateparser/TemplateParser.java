package be.webfactor.c3s.master.templateparser;

import java.util.Map;

import be.webfactor.c3s.master.domain.TemplateEngine;

/**
 * The template parser is an abstract parser that converts a template to String based content, given an object context.
 */
public interface TemplateParser {

	/**
	 * Parses the given template with the given name and contents to a String based on the provided object context.
	 * The base URL is also provided, when the template includes other templates.
	 */
	String parse(String templateName, String templateContents, Map<String, Object> context, String baseUrl) throws TemplateParserException;

	/**
	 * Returns the template engine for which this template parser is a suitable parser.
	 */
	TemplateEngine getTemplateEngine();
}