package be.webfactor.c3s.master.domain;

public enum TemplateEngine {

	FREEMARKER, VELOCITY, THYMELEAF;

	public static TemplateEngine getDefault() {
		return FREEMARKER;
	}
}
