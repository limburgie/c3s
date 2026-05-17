package be.webfactor.c3s.templateparser;

public class TemplateEngineNotSupportedException extends RuntimeException {

	TemplateEngineNotSupportedException(TemplateEngine templateEngine) {
		super(String.format("No template parser found for template engine %s", templateEngine));
	}
}
