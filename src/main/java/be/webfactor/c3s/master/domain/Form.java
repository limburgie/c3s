package be.webfactor.c3s.master.domain;

public class Form {

	private final String name;
	private final Email visitorEmail;
	private final Email managerEmail;

	public Form(String name, Email visitorEmail, Email managerEmail) {
		this.name = name;
		this.visitorEmail = visitorEmail;
		this.managerEmail = managerEmail;
	}

	public String getName() {
		return name;
	}

	public Email getVisitorEmail() {
		return visitorEmail;
	}

	public Email getManagerEmail() {
		return managerEmail;
	}
}
