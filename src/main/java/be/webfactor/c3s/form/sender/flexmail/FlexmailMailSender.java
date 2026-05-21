package be.webfactor.c3s.form.sender.flexmail;

import be.webfactor.c3s.form.sender.EmailMessage;
import be.webfactor.c3s.form.sender.MailSender;
import be.webfactor.c3s.form.sender.MailSenderType;
import be.webfactor.c3s.siteassetstore.domain.MailSettings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class FlexmailMailSender implements MailSender {

	private static final String FLEXMAIL_API_URL = "https://email-api.flexmail.eu/messages";

	private final RestClient restClient = RestClient.create();

	@Override
	public void send(MailSettings settings, EmailMessage message) {
		FlexmailAddress sender = new FlexmailAddress(settings.fromAddress(), message.from().name());
		FlexmailAddress receiver = new FlexmailAddress(message.to().address(), message.to().name());
		FlexmailAddress replyTo = message.replyTo() == null ? null : new FlexmailAddress(message.replyTo().address(), message.replyTo().name());

        restClient.post()
				.uri(FLEXMAIL_API_URL)
				.header(HttpHeaders.AUTHORIZATION, basicAuth(settings.username(), settings.password()))
				.contentType(MediaType.APPLICATION_JSON)
				.body(new FlexmailSendEmailRequest(sender, receiver, replyTo, message.subject(), message.body()))
				.retrieve()
				.toBodilessEntity();
	}

	@Override
	public MailSenderType getType() {
		return MailSenderType.FLEXMAIL;
	}

	private String basicAuth(String username, String password) {
		String credentials = username + ":" + password;
		return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
	}
}
