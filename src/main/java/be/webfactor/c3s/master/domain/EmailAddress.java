package be.webfactor.c3s.master.domain;

public class EmailAddress {

	private final String name;
	private final String address;

	public EmailAddress(String name, String address) {
		this.name = name;
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}
}
