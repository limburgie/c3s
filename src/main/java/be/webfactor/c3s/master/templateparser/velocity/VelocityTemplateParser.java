package be.webfactor.c3s.master.templateparser.velocity;

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.ToolManager;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.master.domain.TemplateEngine;
import be.webfactor.c3s.master.templateparser.TemplateParser;
import be.webfactor.c3s.master.templateparser.TemplateParserException;

@Service
public class VelocityTemplateParser implements TemplateParser {

	private VelocityEngine velocityEngine;

	public VelocityTemplateParser() {
		velocityEngine = new VelocityEngine();
		velocityEngine.init();
	}

	public String parse(String template, Map<String, Object> context) throws TemplateParserException {
		VelocityContext velocityContext = new VelocityContext(context, new ToolManager().createContext());

		StringWriter stringWriter = new StringWriter();
		velocityEngine.evaluate(velocityContext, stringWriter, "", template);

		return stringWriter.toString();
	}

	public TemplateEngine getTemplateEngine() {
		return TemplateEngine.VELOCITY;
	}
}
