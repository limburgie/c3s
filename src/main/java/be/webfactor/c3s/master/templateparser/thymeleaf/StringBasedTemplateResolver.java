package be.webfactor.c3s.master.templateparser.thymeleaf;

import org.thymeleaf.templateresolver.TemplateResolver;

public class StringBasedTemplateResolver extends TemplateResolver {

	public StringBasedTemplateResolver() {
		super();
		setResourceResolver(new StringBasedResourceResolver());
	}
}