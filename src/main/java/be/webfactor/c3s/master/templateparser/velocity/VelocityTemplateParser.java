package be.webfactor.c3s.master.templateparser.velocity;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.URLResourceLoader;
import org.apache.velocity.tools.ToolManager;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.master.domain.TemplateEngine;
import be.webfactor.c3s.master.templateparser.TemplateParser;
import be.webfactor.c3s.master.templateparser.TemplateParserException;

@Service
public class VelocityTemplateParser implements TemplateParser {

	public String parse(String templateName, String templateContents, Map<String, Object> context, String baseUrl) throws TemplateParserException {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty("resource.loader", "mine");
		velocityEngine.setProperty("mine.resource.loader.instance", new URLResourceLoader() {
			public synchronized InputStream getResourceStream(String name) throws ResourceNotFoundException {
				if ("VM_global_library.vm".equals(name)) {
					return super.getResourceStream(name);
				}

				try {
					return new URI(baseUrl + "/" + name).toURL().openStream();
				} catch (URISyntaxException | IOException e) {
					return null;
				}
			}
		});
		velocityEngine.init();

		VelocityContext velocityContext = new VelocityContext(context, new ToolManager().createContext());

		StringWriter stringWriter = new StringWriter();
		velocityEngine.evaluate(velocityContext, stringWriter, templateName, templateContents);

		return stringWriter.toString();
	}

	public TemplateEngine getTemplateEngine() {
		return TemplateEngine.VELOCITY;
	}
}