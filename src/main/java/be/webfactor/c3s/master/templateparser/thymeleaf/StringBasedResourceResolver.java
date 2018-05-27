package be.webfactor.c3s.master.templateparser.thymeleaf;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;

public class StringBasedResourceResolver implements IResourceResolver {

	private static final String NAME = "STRING";

	public String getName() {
		return NAME;
	}

	public InputStream getResourceAsStream(TemplateProcessingParameters templateProcessingParameters, String resourceName) {
		return new ByteArrayInputStream(templateProcessingParameters.getTemplateName().getBytes(StandardCharsets.UTF_8));
	}
}