package be.webfactor.c3s.form.sender.smtp;

import be.webfactor.c3s.form.sender.EmailMessage;
import be.webfactor.c3s.form.sender.MailSender;
import be.webfactor.c3s.form.sender.MailSenderType;
import be.webfactor.c3s.siteassetstore.domain.mail.MailSettings;
import be.webfactor.c3s.siteassetstore.domain.mail.SmtpMailSettings;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Service
public class SmtpMailSender implements MailSender {

	@Override
	public void send(MailSettings settings, EmailMessage message) {
		SmtpMailSettings smtp = (SmtpMailSettings) settings;

		MimeMessagePreparator preparator = mimeMessage -> {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

			helper.setFrom(message.from().address(), message.from().name());
			helper.addTo(message.to().address(), message.to().name());

			if (message.replyTo() != null) {
				helper.setReplyTo(message.replyTo().address(), message.replyTo().name());
			}

			helper.setSubject(message.subject());
			helper.setText(message.body(), true);
		};

		buildJavaMailSender(smtp).send(preparator);
	}

	@Override
	public MailSenderType getType() {
		return MailSenderType.SMTP;
	}

	private JavaMailSenderImpl buildJavaMailSender(SmtpMailSettings settings) {
		JavaMailSenderImpl sender = new JavaMailSenderImpl();

		Properties props = new Properties();
		props.put("mail.smtp.auth", true);
		props.put("mail.smtp.starttls.enable", true);
		props.put("mail.smtp.starttls.required", true);

		sender.setJavaMailProperties(props);
		sender.setHost(settings.host());
		sender.setPort(settings.port());
		sender.setProtocol("smtp");
		sender.setUsername(settings.username());
		sender.setPassword(settings.password());

		return sender;
	}
}
