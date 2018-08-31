package be.webfactor.c3s.master.templateparser.velocity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;

public class VelocityTemplateParserParseTest {

	private VelocityTemplateParser parser = new VelocityTemplateParser();

	@Test
	public void helloWorldSample() {
		String template = "Hello, ${name}!!";

		String result = parser.parse(null, template, Collections.singletonMap("name", "Peter"), null);

		assertThat(result, is("Hello, Peter!!"));
	}

	@Test
	public void helloWorldWithTool() {
		String template = "3 + 2 = $math.add(3, 2)";

		String result = parser.parse(null, template, Collections.emptyMap(), null);

		assertThat(result, is("3 + 2 = 5"));
	}
}