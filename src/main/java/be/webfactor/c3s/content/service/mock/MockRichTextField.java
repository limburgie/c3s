package be.webfactor.c3s.content.service.mock;

import be.webfactor.c3s.content.service.domain.RichTextField;

public class MockRichTextField extends RichTextField {

	private String value;

	MockRichTextField(String value) {
		this.value = value;
	}

	public String getHtml() {
		return "<p>" + value + ". " + MockRandomGenerator.alinea() + "</p><p>" + MockRandomGenerator.alinea() + "</p>";
	}
}