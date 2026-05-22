package be.webfactor.c3s.form.sender;

public class MailSenderNotFoundException extends RuntimeException {

	public MailSenderNotFoundException(MailSenderType type) {
		super("No MailSender implementation found for type: " + type);
	}
}
