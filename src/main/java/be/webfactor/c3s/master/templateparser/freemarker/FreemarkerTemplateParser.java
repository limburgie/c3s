package be.webfactor.c3s.master.templateparser.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.springframework.stereotype.Service;

import be.webfactor.c3s.master.domain.TemplateEngine;
import be.webfactor.c3s.master.templateparser.TemplateParser;
import be.webfactor.c3s.master.templateparser.TemplateParserException;
import freemarker.cache.URLTemplateLoader;
import freemarker.template.*;

@Service
public class FreemarkerTemplateParser implements TemplateParser {

	public String parse(String templateName, String templateContents, Map<String, Object> context, String baseUrl) {
		try {
			Configuration configuration = new Configuration(Configuration.getVersion());
			configuration.setDefaultEncoding("UTF-8");
			configuration.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
			configuration.setLocalizedLookup(false);

			configuration.setTemplateLoader(new URLTemplateLoader() {
				protected URL getURL(String s) {
					try {
						return new URI(baseUrl + "/" + s).toURL();
					} catch (URISyntaxException | MalformedURLException e) {
						return null;
					}
				}

			});

			Template templateObj = new Template(templateName, templateContents, configuration);
			StringWriter stringWriter = new StringWriter();

			templateObj.process(context, stringWriter);

			return stringWriter.toString();
		} catch (TemplateException | IOException e) {
			throw new TemplateParserException(e);
		}
	}

	public TemplateEngine getTemplateEngine() {
		return TemplateEngine.FREEMARKER;
	}
}