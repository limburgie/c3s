package be.webfactor.c3s.templateparser.freemarker;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.stereotype.Service;

import be.webfactor.c3s.templateparser.TemplateEngine;
import be.webfactor.c3s.siteassetstore.SiteAssetNotFoundException;
import be.webfactor.c3s.siteassetstore.SiteAssetStore;
import be.webfactor.c3s.templateparser.TemplateParser;
import be.webfactor.c3s.templateparser.TemplateParserException;
import freemarker.cache.TemplateLoader;
import freemarker.template.*;

@Service
public class FreemarkerTemplateParser implements TemplateParser {

	public String parse(String templateName, String templateContents, Map<String, Object> context, final SiteAssetStore siteAssetStore) {
		try {
			Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
			configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
			configuration.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
			configuration.setLocalizedLookup(false);

			configuration.setTemplateLoader(new TemplateLoader() {
				public Object findTemplateSource(String name) {
					try {
						return siteAssetStore.readResource(name);
					} catch (SiteAssetNotFoundException e) {
						return null;
					}
				}

				public long getLastModified(Object templateSource) {
					return -1;
				}

				public Reader getReader(Object templateSource, String encoding) {
					return new StringReader((String) templateSource);
				}

				public void closeTemplateSource(Object templateSource) {
					// nothing to close
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
