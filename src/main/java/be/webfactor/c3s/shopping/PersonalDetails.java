package be.webfactor.c3s.shopping;

import java.io.Serializable;

public class PersonalDetails implements Serializable {

	private final String name;
	private final String email;

	public PersonalDetails() {
		this("", "");
	}

	public PersonalDetails(String name, String email) {
		this.name = name;
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}
}
