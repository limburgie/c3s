package be.webfactor.c3s.form;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.siteassetstore.SiteAssetStore;
import be.webfactor.c3s.templateparser.TemplateParser;
import be.webfactor.c3s.templateparser.TemplateParserFactory;
import be.webfactor.c3s.contentrepository.ContentRepository;
import be.webfactor.c3s.contentrepository.ContentRepositoryConnection;
import be.webfactor.c3s.contentrepository.ContentRepositoryFactory;
import be.webfactor.c3s.form.captcha.RecaptchaChecker;
import be.webfactor.c3s.form.sender.MailSender;
import be.webfactor.c3s.form.sender.MailSenderFactory;

@Service
@RequiredArgsConstructor
public class FormHandlerFactory {

	private final TemplateParserFactory templateParserFactory;
	private final ContentRepositoryFactory contentRepositoryFactory;
	private final RecaptchaChecker recaptchaChecker;
	private final MailSenderFactory mailSenderFactory;

	public FormHandler forSiteAssetStore(SiteAssetStore siteAssetStore) {
		TemplateParser templateParser = templateParserFactory.forTemplateEngine(siteAssetStore.getTemplateEngine());

		ContentRepositoryConnection repoConnection = siteAssetStore.getContentRepositoryConnection();
		ContentRepository contentRepository = repoConnection == null ? null : contentRepositoryFactory.forConnection(repoConnection);

		MailSender mailSender = mailSenderFactory.forSettings(siteAssetStore.getMailSettings());

		return new FormHandler(siteAssetStore, contentRepository, templateParser, recaptchaChecker, mailSender);
	}
}
