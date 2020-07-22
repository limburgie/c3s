package be.webfactor.c3s.master.domain;

public class Form {

	private final String name;
	private final String contents;

	public Form(String name, String contents) {
		this.name = name;
		this.contents = contents;
	}

	public String getName() {
		return name;
	}

	public String getContents() {
		return contents;
	}
}
