package be.webfactor.c3s.master.templateparser;

import java.util.Map;

import be.webfactor.c3s.master.domain.TemplateEngine;

/**
 * The template parser is an abstract parser that converts a template to String based content, given an object context.
 */
public interface TemplateParser {

	/**
	 * Parses a given template to a String based on the provided object context.
	 * If a problem occurs while parsing, an exception is thrown.
	 */
	String parse(String template, Map<String, Object> context) throws TemplateParserException;

	/**
	 * Returns the template engine for which this template parser is a suitable parser.
	 */
	TemplateEngine getTemplateEngine();
}
