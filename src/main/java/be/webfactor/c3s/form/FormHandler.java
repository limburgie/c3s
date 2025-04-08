package be.webfactor.c3s.form;

import be.webfactor.c3s.content.service.ContentService;
import be.webfactor.c3s.form.captcha.RecaptchaChecker;
import be.webfactor.c3s.master.domain.EmailAddress;
import be.webfactor.c3s.master.domain.Form;
import be.webfactor.c3s.master.domain.LocationThreadLocal;
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
	private static final String LANGUAGE_VAR = "language";

	private final MasterService masterService;
	private final TemplateParser templateParser;
	private final ContentService contentService;
	private final RecaptchaChecker recaptchaChecker;

	FormHandler(MasterService masterService, ContentService contentService, TemplateParser templateParser, RecaptchaChecker recaptchaChecker) {
		this.masterService = masterService;
		this.contentService = contentService;
		this.templateParser = templateParser;
		this.recaptchaChecker = recaptchaChecker;
	}

	public void handleForm(Form form, FormParams formParams) {
		recaptchaChecker.validate(formParams.getValue("captcha"));

		EmailAddress managerEmailAddress = new EmailAddress(masterService.getSiteName(), masterService.getMailSettings().getUsername());
		EmailAddress visitorEmailAddress = new EmailAddress(formParams.getValue("name"), formParams.getValue("email"));

		sendVisitorEmail(managerEmailAddress, visitorEmailAddress, form, formParams);
		sendManagerEmail(managerEmailAddress, visitorEmailAddress, form, formParams);
	}

	private void sendVisitorEmail(EmailAddress managerEmailAddress, EmailAddress visitorEmailAddress, Form form, FormParams formParams) {
		if (form.getVisitorEmail() == null) {
			return;
		}

		String subject = form.getVisitorEmail().getSubject();
		String body = parseTemplate(form.getName() + (" (Visitor)"), form.getVisitorEmail().getContents(), formParams);

		sendEmail(managerEmailAddress, visitorEmailAddress, managerEmailAddress, subject, body);
	}

	private void sendManagerEmail(EmailAddress managerEmailAddress, EmailAddress visitorEmailAddress, Form form, FormParams formParams) {
		if (form.getManagerEmail() == null) {
			return;
		}

		String subject = form.getManagerEmail().getSubject();
		String body = parseTemplate(form.getName() + (" (Manager)"), form.getManagerEmail().getContents(), formParams);

		sendEmail(managerEmailAddress, managerEmailAddress, visitorEmailAddress, subject, body);
	}

	private String parseTemplate(String templateName, String templateContents, FormParams formParams) {
		HashMap<String, Object> context = new HashMap<>();
		context.put(FORM_PARAMS_VAR, formParams);
		context.put(API_TEMPLATE_VAR, contentService == null ? null : contentService.getApi());
		context.put(CART_PARAM, ShoppingCartThreadLocal.getShoppingCart());
		context.put(I18N_TEMPLATE_VAR, new I18n(masterService.getResourceBundle()));
		context.put(LANGUAGE_VAR, LocationThreadLocal.getLocaleContext().getLocale().getLanguage());

		return templateParser.parse(templateName, templateContents, context, masterService.getBaseUrl());
	}

	private void sendEmail(EmailAddress from, EmailAddress to, EmailAddress replyTo, String subject, String body) {
		MimeMessagePreparator mailMessage = mimeMessage -> {
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

			message.setFrom(from.getAddress(), from.getName());
			message.addTo(to.getAddress(), to.getName());

			if (replyTo != null) {
				message.setReplyTo(replyTo.getAddress(), replyTo.getName());
			}

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
