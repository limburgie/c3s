package be.webfactor.c3s.siteassetstore.filebased;

import be.webfactor.c3s.siteassetstore.SiteAssetNotFoundException;
import be.webfactor.c3s.siteassetstore.SiteAssetStore;
import be.webfactor.c3s.siteassetstore.SiteAssetStoreConnection;
import be.webfactor.c3s.siteassetstore.cache.SiteAssetStoreFileCache;
import be.webfactor.c3s.siteassetstore.domain.*;
import be.webfactor.c3s.siteassetstore.filebased.domain.*;
import be.webfactor.c3s.contentrepository.ContentRepositoryConnection;
import be.webfactor.c3s.contentrepository.ContentRepositoryType;
import be.webfactor.c3s.controller.PageController;
import be.webfactor.c3s.controller.exception.PageNotFoundException;
import be.webfactor.c3s.templateparser.TemplateEngine;
import com.google.gson.Gson;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractFileBasedSiteAssetStore implements SiteAssetStore {

	private static final String CONFIG_FILE = "c3s.json";
	private static final String I18N_BASE_NAME = "i18n";

	@Autowired private SiteAssetStoreFileCache cache;

	private String basePath;
	private SiteConfiguration config;

	public void initialize(SiteAssetStoreConnection connection) {
		basePath = connection.getRepositoryId();
		config = new Gson().fromJson(readResource(CONFIG_FILE), SiteConfiguration.class);

		if (config.getLocationSettings() != null) {
			LocationThreadLocal.setTimeZone(ZoneId.of(config.getLocationSettings().getTimeZone()));
		}
	}

	public String getSiteName() {
		return config.getName();
	}

	public List<Page> getPages(boolean all) {
		return config.getPages().stream().filter(page -> all || !page.isHidden()).map(pageMapper(false)).collect(Collectors.toList());
	}

	public TemplateEngine getTemplateEngine() {
		return TemplateEngine.get(config.getTemplateEngine());
	}

	public ContentRepositoryConnection getContentRepositoryConnection() {
		SiteContentRepositoryConnection repositoryConnection = config.getContentRepositoryConnection();

		if (repositoryConnection == null) {
			return null;
		}

		return ContentRepositoryConnection.of(
				ContentRepositoryType.valueOf(repositoryConnection.getType()),
				repositoryConnection.getRepositoryId(),
				repositoryConnection.getAccessToken());
	}

	public MailSettings getMailSettings() {
		SiteMailSettings s = config.getMailSettings();

		if (s == null) {
			return null;
		}

		return new MailSettings(s.getHost(), s.getPort(), s.getUsername(), s.getPassword());
	}

	public Page getPage(String friendlyUrl) {
		if (friendlyUrl == null) {
			throw new PageNotFoundException();
		}

		List<Page> pages = config.getAllPages().stream()
				.filter(siteRequest -> friendlyUrl.equals(siteRequest.getFriendlyUrl()))
				.map(pageMapper(true)).toList();

		if (pages.isEmpty()) {
			throw new PageNotFoundException();
		}

		return pages.get(0);
	}

	public Page getIndexPage() {
		String indexPageFriendlyUrl = config.getIndexPage();

		if (indexPageFriendlyUrl != null) {
			return getPage(config.getIndexPage());
		}

		return config.getPages().stream().findFirst().map(pageMapper(true)).orElse(null);
	}

	public Page getErrorPage() {
		SitePage errorPage = config.getErrorPage();

		Page.PageBuilder pageBuilder = Page.builder().name(errorPage.getName()).hidden(true).indexPage(false);

		if (errorPage.isTemplated()) {
			pageBuilder = pageBuilder.template(getTemplate(errorPage.getTemplate())).inserts(readInserts(errorPage.getInserts()));
		} else {
			pageBuilder.contents(readResource(errorPage.getContents()));
		}

		return pageBuilder.build();
	}

	private Function<SitePage, Page> pageMapper(boolean withContents) {
		return sitePage -> {
			String friendlyUrl = sitePage.getFriendlyUrl();
			String name = sitePage.getName();
			boolean hidden = sitePage.isHidden();

			Page.PageBuilder pageBuilder = Page.builder()
					.friendlyUrl(friendlyUrl)
					.name(name)
					.hidden(hidden)
					.indexPage(friendlyUrl.equals(config.getIndexPage()));

			if (!withContents) {
				List<Page> children = sitePage.getChildren().stream().filter(page -> !page.isHidden()).map(pageMapper(false)).collect(Collectors.toList());

				pageBuilder = pageBuilder.children(children);
			} else if (sitePage.isTemplated()) {
				Template template = getTemplate(sitePage.getTemplate());
				Map<String, String> inserts = readInserts(sitePage.getInserts());

				pageBuilder = pageBuilder.template(template).inserts(inserts);
			} else {
				pageBuilder = pageBuilder.contents(readResource(sitePage.getContents()));
			}

			return pageBuilder.build();
		};
	}

	private Template getTemplate(String name) {
		Optional<SiteTemplate> template = config.getTemplates().stream().filter(t -> name.equals(t.getName())).findFirst();

		if (template.isPresent()) {
			SiteTemplate siteTemplate = template.get();

			String extendsFrom = siteTemplate.getExtendsFrom();

			if (extendsFrom == null) {
				String templateFile = siteTemplate.getContents();

				return new Template(name, readResource(templateFile));
			} else {
				return new Template(name, getTemplate(extendsFrom), readInserts(siteTemplate.getInserts()));
			}
		}

		return null;
	}

	private Map<String, String> readInserts(Map<String, String> inserts) {
		Map<String, String> readInserts = new HashMap<>();

		for (Map.Entry<String, String> entry : inserts.entrySet()) {
			readInserts.put(entry.getKey(), readResource(entry.getValue()));
		}

		return readInserts;
	}

	public String getAssetUrl(String assetPath) {
		return PageController.ASSETS_PREFIX + assetPath;
	}

	public Form getForm(String name) {
		if (name == null) {
			return getFirstForm();
		}

		return doGetForm(name).orElse(getFirstForm());
	}

	private Optional<Form> doGetForm(String name) {
		return config.getForms().stream().filter(siteForm -> name.equals(siteForm.getName())).map(this::fromSiteForm).findFirst();
	}

	private Form getFirstForm() {
		if (config.getForms().isEmpty()) {
			return null;
		}

		return fromSiteForm(config.getForms().get(0));
	}

	private Form fromSiteForm(SiteForm siteForm) {
		String formName = siteForm.getName();
		Email visitorEmail = fromSiteEmail(siteForm.getVisitorEmail());
		Email managerEmail = fromSiteEmail(siteForm.getManagerEmail());

		return new Form(formName, visitorEmail, managerEmail);
	}

	private Email fromSiteEmail(SiteEmail siteEmail) {
		String subject = getEmailSubject(siteEmail);
		String contents = getEmailContents(siteEmail);

		return new Email(subject, contents);
	}

	private String getEmailContents(SiteEmail siteEmail) {
		Locale locale = LocationThreadLocal.getLocaleContext().locale();

		if (locale == null) {
			return readResource(siteEmail.getContents());
		} else {
			String contentsWithoutExtension = FilenameUtils.removeExtension(siteEmail.getContents());
			String contentsExtension = FilenameUtils.getExtension(siteEmail.getContents());

			try {
				return readResource(contentsWithoutExtension + "_" + locale.getLanguage() + "." + contentsExtension);
			} catch (Exception e) {
				return readResource(siteEmail.getContents());
			}
		}
	}

	private String getEmailSubject(SiteEmail siteEmail) {
		ResourceBundle resourceBundle = getResourceBundle();
		String subject = siteEmail.getSubject();

		try {
			subject = resourceBundle == null ? subject : resourceBundle.getString(subject);
		} catch (MissingResourceException ignored) {}

		return subject;
	}

	public String getBaseUrl() {
		return basePath;
	}

	public ResourceBundle getResourceBundle() {
		Locale locale = LocationThreadLocal.getLocaleContext().locale();

		for (String candidate : candidateBundleNames(locale)) {
			try {
				byte[] bytes = readAsset(I18N_BASE_NAME + "/" + candidate);
				return new PropertyResourceBundle(new ByteArrayInputStream(bytes));
			} catch (SiteAssetNotFoundException ignored) {
				// try the next candidate
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return null;
	}

	private List<String> candidateBundleNames(Locale locale) {
		List<String> names = new ArrayList<>();
		if (locale != null) {
			String lang = locale.getLanguage();
			String country = locale.getCountry();
			if (!lang.isEmpty() && !country.isEmpty()) {
				names.add(I18N_BASE_NAME + "_" + lang + "_" + country + ".properties");
			}
			if (!lang.isEmpty()) {
				names.add(I18N_BASE_NAME + "_" + lang + ".properties");
			}
		}
		names.add(I18N_BASE_NAME + ".properties");
		return names;
	}

	public List<Locale> getLocales() {
		return config.getLocales();
	}

	public final String readResource(String relativePath) {
		String normalized = normalize(relativePath);
		return cache.getOrLoadString(basePath, normalized, () -> doReadResource(normalized));
	}

	public final byte[] readAsset(String relativePath) {
		String normalized = normalize(relativePath);
		return cache.getOrLoadBytes(basePath, normalized, () -> doReadAssetBytes(normalized));
	}

	protected abstract String doReadResource(String relativePath);

	protected abstract byte[] doReadAssetBytes(String relativePath);

	private static String normalize(String path) {
		return path.startsWith("/") ? path.substring(1) : path;
	}
}
