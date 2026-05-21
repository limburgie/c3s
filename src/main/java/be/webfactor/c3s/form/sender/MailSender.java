package be.webfactor.c3s.form.sender;

import be.webfactor.c3s.siteassetstore.domain.mail.MailSettings;

public interface MailSender {

	void send(MailSettings settings, EmailMessage message);

	MailSenderType getType();
}
