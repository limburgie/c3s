package be.webfactor.c3s.templateparser;

public enum TemplateEngine {

	FREEMARKER;

	private static final TemplateEngine DEFAULT = FREEMARKER;

	public static TemplateEngine get(String templateEngine) {
		return templateEngine == null ? DEFAULT : valueOf(templateEngine.toUpperCase());
	}
}
