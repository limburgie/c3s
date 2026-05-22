package be.webfactor.c3s.form;

import be.webfactor.c3s.contentrepository.ContentRepository;
import be.webfactor.c3s.form.captcha.RecaptchaChecker;
import be.webfactor.c3s.form.sender.EmailMessage;
import be.webfactor.c3s.form.sender.MailSender;
import be.webfactor.c3s.siteassetstore.domain.EmailAddress;
import be.webfactor.c3s.siteassetstore.domain.Form;
import be.webfactor.c3s.siteassetstore.domain.LocationThreadLocal;
import be.webfactor.c3s.siteassetstore.SiteAssetStore;
import be.webfactor.c3s.templateparser.TemplateParser;
import be.webfactor.c3s.renderer.I18n;
import be.webfactor.c3s.shopping.ShoppingCartThreadLocal;

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
	private final MailSender mailSender;

	FormHandler(SiteAssetStore siteAssetStore, ContentRepository contentRepository, TemplateParser templateParser, RecaptchaChecker recaptchaChecker, MailSender mailSender) {
		this.siteAssetStore = siteAssetStore;
		this.contentRepository = contentRepository;
		this.templateParser = templateParser;
		this.recaptchaChecker = recaptchaChecker;
		this.mailSender = mailSender;
	}

	public void handleForm(Form form, FormParams formParams) {
        if (recaptchaChecker.validate(formParams)) {
			EmailAddress managerEmailAddress = new EmailAddress(siteAssetStore.getSiteName(), siteAssetStore.getMailSettings().fromAddress());
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
		mailSender.send(siteAssetStore.getMailSettings(), new EmailMessage(from, to, replyTo, subject, body));
	}
}
