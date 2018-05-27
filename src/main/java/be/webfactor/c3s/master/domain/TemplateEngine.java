package be.webfactor.c3s.master.domain;

public enum TemplateEngine {

	FREEMARKER, VELOCITY, THYMELEAF;

	private static final TemplateEngine DEFAULT_ENGINE = FREEMARKER;

	public static TemplateEngine get(String templateEngine) {
		return templateEngine == null ? DEFAULT_ENGINE : valueOf(templateEngine.toUpperCase());
	}
}
