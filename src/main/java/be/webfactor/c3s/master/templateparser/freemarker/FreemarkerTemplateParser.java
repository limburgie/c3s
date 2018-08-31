package be.webfactor.c3s.master.templateparser.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.springframework.stereotype.Service;

import be.webfactor.c3s.master.domain.TemplateEngine;
import be.webfactor.c3s.master.templateparser.TemplateParser;
import be.webfactor.c3s.master.templateparser.TemplateParserException;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.*;

@Service
public class FreemarkerTemplateParser implements TemplateParser {

	private Configuration configuration;

	public FreemarkerTemplateParser() {
		configuration = new Configuration(Configuration.getVersion());
		configuration.setDefaultEncoding("UTF-8");
		configuration.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

		BeansWrapper beansWrapper = new BeansWrapperBuilder(Configuration.getVersion()).build();
		configuration.setSharedVariable("statics", beansWrapper.getStaticModels());
		configuration.setSharedVariable("enums", beansWrapper.getEnumModels());
		configuration.setSharedVariable("objectUtil", new ObjectConstructor());
	}

	public String parse(String templateName, String templateContents, Map<String, Object> context) {
		try {
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