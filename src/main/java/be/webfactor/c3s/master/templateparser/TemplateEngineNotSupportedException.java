package be.webfactor.c3s.master.templateparser;

import be.webfactor.c3s.master.domain.TemplateEngine;

public class TemplateEngineNotSupportedException extends RuntimeException {

	public TemplateEngineNotSupportedException(TemplateEngine templateEngine) {
		super(String.format("No template parser found for template engine %s", templateEngine));
	}
}
