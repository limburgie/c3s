package be.webfactor.c3s.siteassetstore.domain.mail;

import be.webfactor.c3s.form.sender.MailSenderType;

public sealed interface MailSettings permits SmtpMailSettings, FlexmailMailSettings {

	MailSenderType type();

	String username();

	String password();

	String fromAddress();
}
