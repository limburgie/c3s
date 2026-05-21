package be.webfactor.c3s.form.sender;

import be.webfactor.c3s.siteassetstore.domain.MailSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MailSenderFactory {

	private final List<MailSender> mailSenders;

	public MailSender forSettings(MailSettings settings) {
		return mailSenders.stream()
				.filter(sender -> sender.getType() == settings.type())
				.findFirst()
				.orElseThrow(() -> new MailSenderNotFoundException(settings.type()));
	}
}
