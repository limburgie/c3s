package be.webfactor.c3s.templateparser;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateParserFactory {

	private final List<TemplateParser> templateParsers;

	public TemplateParser forTemplateEngine(TemplateEngine templateEngine) {
		return templateParsers.stream()
				.filter(templateParser -> templateParser.getTemplateEngine() == templateEngine)
				.findFirst()
				.orElseThrow(() -> new TemplateEngineNotSupportedException(templateEngine));
	}
}
