package be.webfactor.c3s.siteassetstore.domain.mail;

import be.webfactor.c3s.form.sender.MailSenderType;

public record SmtpMailSettings(String host, int port, String username, String password) implements MailSettings {

	@Override
	public MailSenderType type() {
		return MailSenderType.SMTP;
	}

	@Override
	public String fromAddress() {
		return username;
	}
}
