package be.webfactor.c3s.master.domain;

public class EmailAddress {

	public static final String DELIMITER = "|";
	public static final String ESCAPED_DELIMITER = "\\|";

	private final String name;
	private final String address;

	public EmailAddress(String value) {
		if (!value.contains(DELIMITER)) {
			this.name = null;
			this.address = value;
		} else {
			String[] parts = value.split(ESCAPED_DELIMITER);
			this.name = parts[0];
			this.address = parts[1];
		}
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}
}
