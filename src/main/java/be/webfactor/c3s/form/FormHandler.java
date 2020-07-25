package be.webfactor.c3s.form;

import be.webfactor.c3s.content.service.ContentService;
import be.webfactor.c3s.master.domain.EmailAddress;
import be.webfactor.c3s.master.domain.Form;
import be.webfactor.c3s.master.domain.MailSettings;
import be.webfactor.c3s.master.service.MasterService;
import be.webfactor.c3s.master.templateparser.TemplateParser;
import be.webfactor.c3s.shopping.ShoppingCartThreadLocal;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class FormHandler {

	private static final String API_TEMPLATE_VAR = "api";
	private static final String FORM_PARAMS_VAR = "params";
	private static final String CART_PARAM = "cart";

	private final MasterService masterService;
	private final TemplateParser templateParser;
	private final ContentService contentService;

	FormHandler(MasterService masterService, ContentService contentService, TemplateParser templateParser) {
		this.masterService = masterService;
		this.contentService = contentService;
		this.templateParser = templateParser;
	}

	public void handleForm(Form form, FormParams formParams) {
		MailSettings mailSettings = masterService.getMailSettings();
		String subject = buildSubject(form, formParams);
		String body = generateBody(form, formParams);
		EmailAddress fromAndTo = new EmailAddress(masterService.getSiteName(), mailSettings.getUsername());
		List<EmailAddress> ccs = getEmailAddressesFromParameter(formParams, "ccName", "ccAddress");
		List<EmailAddress> bccs = getEmailAddressesFromParameter(formParams, "bccName", "bccAddress");

		sendEmail(mailSettings, subject, body, fromAndTo, ccs, bccs);
	}

	private String buildSubject(Form form, FormParams formParams) {
		String result = formParams.getValue("subject");

		if (result == null) {
			result = String.format("Form %s was submitted", form.getName());
		}

		return result;
	}

	private String generateBody(Form form, FormParams formParams) {
		HashMap<String, Object> context = new HashMap<>();
		context.put(FORM_PARAMS_VAR, formParams);
		context.put(API_TEMPLATE_VAR, contentService == null ? null : contentService.getApi());
		context.put(CART_PARAM, ShoppingCartThreadLocal.getShoppingCart());

		return templateParser.parse(form.getName(), form.getContents(), context, masterService.getBaseUrl());
	}

	private List<EmailAddress> getEmailAddressesFromParameter(FormParams formParams, String nameParam, String addressParam) {
		if (!formParams.containsKey(addressParam)) {
			return Collections.emptyList();
		}

		List<EmailAddress> result = new ArrayList<>();

		List<String> names = formParams.getValues(nameParam);
		List<String> addresses = formParams.getValues(addressParam);

		for (int i = 0; i < addresses.size(); i++) {
			String address = addresses.get(i);
			String name = names.size() <= i ? null : names.get(i);

			result.add(new EmailAddress(name, address));
		}

		return result;
	}

	private void sendEmail(MailSettings mailSettings, String subject, String body, EmailAddress fromAndTo, List<EmailAddress> ccs, List<EmailAddress> bccs) {
		MimeMessagePreparator mailMessage = mimeMessage -> {
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

			message.setFrom(fromAndTo.getAddress(), fromAndTo.getName());
			message.addTo(fromAndTo.getAddress(), fromAndTo.getName());

			for (EmailAddress cc : ccs) {
				message.addCc(cc.getAddress(), cc.getName());
			}

			for (EmailAddress bcc : bccs) {
				message.addBcc(bcc.getAddress(), bcc.getName());
			}

			message.setSubject(subject);
			message.setText(body, true);
		};

		createJavaMailSender(mailSettings).send(mailMessage);
	}

	private JavaMailSender createJavaMailSender(MailSettings mailSettings) {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

		Properties mailProperties = new Properties();
		mailProperties.put("mail.smtp.auth", true);
		mailProperties.put("mail.smtp.starttls.enable", true);
		mailProperties.put("mail.smtp.starttls.required", true);

		mailSender.setJavaMailProperties(mailProperties);
		mailSender.setHost(mailSettings.getHost());
		mailSender.setPort(mailSettings.getPort());
		mailSender.setProtocol("smtp");
		mailSender.setUsername(mailSettings.getUsername());
		mailSender.setPassword(mailSettings.getPassword());

		return mailSender;
	}
}
