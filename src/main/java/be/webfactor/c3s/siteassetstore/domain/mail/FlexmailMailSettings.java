package be.webfactor.c3s.siteassetstore.domain.mail;

import be.webfactor.c3s.form.sender.MailSenderType;

public record FlexmailMailSettings(String username, String password, String fromAddress) implements MailSettings {

	@Override
	public MailSenderType type() {
		return MailSenderType.FLEXMAIL;
	}
}
