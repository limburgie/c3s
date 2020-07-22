package be.webfactor.c3s.master.domain;

public class MailSettings {

	private final String host;
	private final int port;
	private final String username;
	private final String password;

	public MailSettings(String host, int port, String username, String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
