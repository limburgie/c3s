package be.webfactor.c3s.master.domain;

public class Email {

	private final String subject;
	private final String contents;

	public Email(String subject, String contents) {
		this.subject = subject;
		this.contents = contents;
	}

	public String getSubject() {
		return subject;
	}

	public String getContents() {
		return contents;
	}
}
