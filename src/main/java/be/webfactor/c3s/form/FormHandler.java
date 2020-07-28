package be.webfactor.c3s.form;

import be.webfactor.c3s.content.service.ContentService;
import be.webfactor.c3s.master.domain.EmailAddress;
import be.webfactor.c3s.master.domain.Form;
import be.webfactor.c3s.master.domain.MailSettings;
import be.webfactor.c3s.master.service.MasterService;
import be.webfactor.c3s.master.templateparser.TemplateParser;
import be.webfactor.c3s.renderer.I18n;
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
	private static final String I18N_TEMPLATE_VAR = "i18n";

	private final MasterService masterService;
	private final TemplateParser templateParser;
	private final ContentService contentService;

	FormHandler(MasterService masterService, ContentService contentService, TemplateParser templateParser) {
		this.masterService = masterService;
		this.contentService = contentService;
		this.templateParser = templateParser;
	}

	public void handleForm(Form form, FormParams formParams) {
		sendVisitorEmail(form, formParams);
		sendManagerEmail(form, formParams);
	}

	private void sendVisitorEmail(Form form, FormParams formParams) {
		EmailAddress from = new EmailAddress(masterService.getSiteName(), masterService.getMailSettings().getUsername());
		EmailAddress to = new EmailAddress(formParams.getValue("name"), formParams.getValue("email"));

		String subject = form.getVisitorEmail().getSubject();
		String body = parseTemplate(form.getName() + (" (Visitor)"), form.getVisitorEmail().getContents(), formParams);

		sendEmail(from, to, subject, body);
	}

	private void sendManagerEmail(Form form, FormParams formParams) {
		EmailAddress fromAndTo = new EmailAddress(masterService.getSiteName(), masterService.getMailSettings().getUsername());

		String subject = form.getManagerEmail().getSubject();
		String body = parseTemplate(form.getName() + (" (Manager)"), form.getManagerEmail().getContents(), formParams);

		sendEmail(fromAndTo, fromAndTo, subject, body);
	}

	private String buildSubject(Form form, FormParams formParams) {
		String result = formParams.getValue("subject");

		if (result == null) {
			result = String.format("Form %s was submitted", form.getName());
		}

		return result;
	}

	private String parseTemplate(String templateName, String templateContents, FormParams formParams) {
		HashMap<String, Object> context = new HashMap<>();
		context.put(FORM_PARAMS_VAR, formParams);
		context.put(API_TEMPLATE_VAR, contentService == null ? null : contentService.getApi());
		context.put(CART_PARAM, ShoppingCartThreadLocal.getShoppingCart());
		context.put(I18N_TEMPLATE_VAR, new I18n(masterService.getResourceBundle()));

		return templateParser.parse(templateName, templateContents, context, masterService.getBaseUrl());
	}

	private void sendEmail(EmailAddress from, EmailAddress to, String subject, String body) {
		MimeMessagePreparator mailMessage = mimeMessage -> {
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

			message.setFrom(from.getAddress(), from.getName());
			message.addTo(to.getAddress(), to.getName());

			message.setSubject(subject);
			message.setText(body, true);
		};

		createJavaMailSender(masterService.getMailSettings()).send(mailMessage);
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
