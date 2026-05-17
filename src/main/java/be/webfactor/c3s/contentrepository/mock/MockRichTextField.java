package be.webfactor.c3s.contentrepository.mock;

import be.webfactor.c3s.contentrepository.domain.RichTextField;

public class MockRichTextField extends RichTextField {

	private String value;

	MockRichTextField(String value) {
		this.value = value;
	}

	public String getHtml() {
		return "<p>" + value + ". " + MockRandomGenerator.alinea() + "</p><p>" + MockRandomGenerator.alinea() + "</p>";
	}
}