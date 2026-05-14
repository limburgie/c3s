package be.webfactor.c3s.form;

import be.webfactor.c3s.contentrepository.ContentRepository;
import be.webfactor.c3s.form.captcha.RecaptchaChecker;
import be.webfactor.c3s.siteassetstore.domain.EmailAddress;
import be.webfactor.c3s.siteassetstore.domain.Form;
import be.webfactor.c3s.siteassetstore.domain.LocationThreadLocal;
import be.webfactor.c3s.siteassetstore.domain.MailSettings;
import be.webfactor.c3s.siteassetstore.SiteAssetStore;
import be.webfactor.c3s.templateparser.TemplateParser;
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

	private final SiteAssetStore siteAssetStore;
	private final TemplateParser templateParser;
	private final ContentRepository contentRepository;
	private final RecaptchaChecker recaptchaChecker;

	FormHandler(SiteAssetStore siteAssetStore, ContentRepository contentRepository, TemplateParser templateParser, RecaptchaChecker recaptchaChecker) {
		this.siteAssetStore = siteAssetStore;
		this.contentRepository = contentRepository;
		this.templateParser = templateParser;
		this.recaptchaChecker = recaptchaChecker;
	}

	public void handleForm(Form form, FormParams formParams) {
        if (recaptchaChecker.validate(formParams)) {
			EmailAddress managerEmailAddress = new EmailAddress(siteAssetStore.getSiteName(), siteAssetStore.getMailSettings().username());
			EmailAddress visitorEmailAddress = new EmailAddress(formParams.getValue("name"), formParams.getValue("email"));

			sendVisitorEmail(managerEmailAddress, visitorEmailAddress, form, formParams);
			sendManagerEmail(managerEmailAddress, visitorEmailAddress, form, formParams);
		}
	}

	private void sendVisitorEmail(EmailAddress managerEmailAddress, EmailAddress visitorEmailAddress, Form form, FormParams formParams) {
		if (form.visitorEmail() == null) {
			return;
		}

		String subject = form.visitorEmail().subject();
		String body = parseTemplate(form.name() + (" (Visitor)"), form.visitorEmail().contents(), formParams);

		sendEmail(managerEmailAddress, visitorEmailAddress, managerEmailAddress, subject, body);
	}

	private void sendManagerEmail(EmailAddress managerEmailAddress, EmailAddress visitorEmailAddress, Form form, FormParams formParams) {
		if (form.managerEmail() == null) {
			return;
		}

		String subject = form.managerEmail().subject();
		String body = parseTemplate(form.name() + (" (Manager)"), form.managerEmail().contents(), formParams);

		sendEmail(managerEmailAddress, managerEmailAddress, visitorEmailAddress, subject, body);
	}

	private String parseTemplate(String templateName, String templateContents, FormParams formParams) {
		HashMap<String, Object> context = new HashMap<>();
		context.put(FORM_PARAMS_VAR, formParams);
		context.put(API_TEMPLATE_VAR, contentRepository == null ? null : contentRepository.getApi());
		context.put(CART_PARAM, ShoppingCartThreadLocal.getShoppingCart());
		context.put(I18N_TEMPLATE_VAR, new I18n(siteAssetStore.getResourceBundle()));
		context.put(LANGUAGE_VAR, LocationThreadLocal.getLocaleContext().locale().getLanguage());

		return templateParser.parse(templateName, templateContents, context, siteAssetStore);
	}

	private void sendEmail(EmailAddress from, EmailAddress to, EmailAddress replyTo, String subject, String body) {
		MimeMessagePreparator mailMessage = mimeMessage -> {
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

			message.setFrom(from.address(), from.name());
			message.addTo(to.address(), to.name());

			if (replyTo != null) {
				message.setReplyTo(replyTo.address(), replyTo.name());
			}

			message.setSubject(subject);
			message.setText(body, true);
		};

		createJavaMailSender(siteAssetStore.getMailSettings()).send(mailMessage);
	}

	private JavaMailSender createJavaMailSender(MailSettings mailSettings) {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

		Properties mailProperties = new Properties();
		mailProperties.put("mail.smtp.auth", true);
		mailProperties.put("mail.smtp.starttls.enable", true);
		mailProperties.put("mail.smtp.starttls.required", true);

		mailSender.setJavaMailProperties(mailProperties);
		mailSender.setHost(mailSettings.host());
		mailSender.setPort(mailSettings.port());
		mailSender.setProtocol("smtp");
		mailSender.setUsername(mailSettings.username());
		mailSender.setPassword(mailSettings.password());

		return mailSender;
	}
}
