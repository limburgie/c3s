package be.webfactor.c3s.master.service.webserver;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import be.webfactor.c3s.controller.exception.PageNotFoundException;
import be.webfactor.c3s.master.domain.*;
import be.webfactor.c3s.master.service.webserver.domain.*;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import be.webfactor.c3s.controller.PageController;
import be.webfactor.c3s.master.service.MasterService;
import be.webfactor.c3s.master.service.webserver.i18n.UTF8Control;
import be.webfactor.c3s.repository.RepositoryConnection;
import be.webfactor.c3s.repository.RepositoryType;

@Service
@Scope("request")
public class WebserverMasterService implements MasterService {

	private static final String CONFIG_FILE = "c3s.json";
	private static final String I18N_BASE_NAME = "i18n";

	private String basePath;
	private WebserverSiteConfiguration config;

	public void initialize(RepositoryConnection connection) {
		basePath = connection.getRepositoryId();
		config = new Gson().fromJson(readFile(CONFIG_FILE), WebserverSiteConfiguration.class);

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

	public RepositoryConnection getRepositoryConnection() {
		WebserverSiteContentRepositoryConnection repositoryConnection = config.getContentRepositoryConnection();

		if (repositoryConnection == null) {
			return null;
		}

		RepositoryType type = RepositoryType.valueOf(repositoryConnection.getType());
		String id = repositoryConnection.getRepositoryId();
		String accessToken = repositoryConnection.getAccessToken();

		return new RepositoryConnection(type, id, accessToken);
	}

	public MailSettings getMailSettings() {
		WebserverSiteMailSettings s = config.getMailSettings();

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
				.filter(webserverSitePage -> friendlyUrl.equals(webserverSitePage.getFriendlyUrl()))
				.map(pageMapper(true)).collect(Collectors.toList());

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
		WebserverSitePage errorPage = config.getErrorPage();

		if (errorPage.isTemplated()) {
			return new Page(errorPage.getName(), getTemplate(errorPage.getTemplate()), readInserts(errorPage.getInserts()));
		}

		return new Page(errorPage.getName(), readFile(errorPage.getContents()));
	}

	private Function<WebserverSitePage, Page> pageMapper(boolean withContents) {
		return webserverSitePage -> {
			String friendlyUrl = webserverSitePage.getFriendlyUrl();
			String name = webserverSitePage.getName();
			boolean hidden = webserverSitePage.isHidden();

			if (!withContents) {
				List<Page> children = webserverSitePage.getChildren().stream().filter(page -> !page.isHidden()).map(pageMapper(false)).collect(Collectors.toList());

				return new Page(friendlyUrl, hidden, name, children);
			}

			if (webserverSitePage.isTemplated()) {
				Template template = getTemplate(webserverSitePage.getTemplate());
				Map<String, String> inserts = readInserts(webserverSitePage.getInserts());

				return new Page(friendlyUrl, hidden, name, template, inserts);
			}

			return new Page(friendlyUrl, hidden, name, readFile(webserverSitePage.getContents()));
		};
	}

	private Template getTemplate(String name) {
		Optional<WebserverSiteTemplate> template = config.getTemplates().stream().filter(t -> name.equals(t.getName())).findFirst();

		if (template.isPresent()) {
			WebserverSiteTemplate webserverSiteTemplate = template.get();

			String extendsFrom = webserverSiteTemplate.getExtendsFrom();

			if (extendsFrom == null) {
				String templateFile = webserverSiteTemplate.getContents();

				return new Template(name, readFile(templateFile));
			} else {
				return new Template(name, getTemplate(extendsFrom), readInserts(webserverSiteTemplate.getInserts()));
			}
		}

		return null;
	}

	private Map<String, String> readInserts(Map<String, String> inserts) {
		Map<String, String> readInserts = new HashMap<>();

		for (Map.Entry<String, String> entry : inserts.entrySet()) {
			readInserts.put(entry.getKey(), readFile(entry.getValue()));
		}

		return readInserts;
	}

	public String getAssetUrl(String assetPath) {
		return basePath + PageController.ASSETS_PREFIX + assetPath;
	}

	public Form getForm(String name) {
		if (name == null) {
			return getFirstForm();
		}

		return doGetForm(name).orElse(getFirstForm());
	}

	private Optional<Form> doGetForm(String name) {
		return config.getForms().stream().filter(webserverSiteForm -> name.equals(webserverSiteForm.getName())).map(this::fromWebserverSiteForm).findFirst();
	}

	private Form getFirstForm() {
		if (config.getForms().isEmpty()) {
			return null;
		}

		return fromWebserverSiteForm(config.getForms().get(0));
	}

	private Form fromWebserverSiteForm(WebserverSiteForm webserverSiteForm) {
		String formName = webserverSiteForm.getName();
		Email visitorEmail = fromWebserverSiteEmail(webserverSiteForm.getVisitorEmail());
		Email managerEmail = fromWebserverSiteEmail(webserverSiteForm.getManagerEmail());

		return new Form(formName, visitorEmail, managerEmail);
	}

	private Email fromWebserverSiteEmail(WebserverSiteEmail webserverSiteEmail) {
		ResourceBundle resourceBundle = getResourceBundle();

		String subject = webserverSiteEmail.getSubject();
		subject = resourceBundle == null ? subject : resourceBundle.getString(subject);
		String contents = readFile(webserverSiteEmail.getContents());

		return new Email(subject, contents);
	}

	private String readFile(String path) {
		try {
			return IOUtils.toString(getURI(path), Charset.defaultCharset());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private URI getURI(String path) {
		try {
			return new URI(basePath + "/" + path);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private URL getURL(String path) {
		try {
			return getURI(path).toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public RepositoryType getType() {
		return RepositoryType.WEB_SERVER;
	}

	public String getBaseUrl() {
		return basePath;
	}

	@SneakyThrows
	public ResourceBundle getResourceBundle() {
		Locale locale = LocationThreadLocal.getLocaleContext().getLocale();
		URL i18nFolder = getURL(I18N_BASE_NAME + "/");
		ClassLoader classLoader = new URLClassLoader(new URL[] {i18nFolder});

		return ResourceBundle.getBundle(I18N_BASE_NAME, locale, classLoader, new UTF8Control());
	}

	public List<Locale> getLocales() {
		if (config.getLocationSettings() == null || config.getLocationSettings().getLocales() == null) {
			return Collections.emptyList();
		}

		return config.getLocationSettings().getLocales().stream()
				.map(LocaleUtils::toLocale)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}
}