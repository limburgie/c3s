package be.webfactor.c3s.master.templateparser;

import java.util.Map;

public interface TemplateParser {

	String parse(String template, Map<String, Object> context) throws TemplateParserException;
}
