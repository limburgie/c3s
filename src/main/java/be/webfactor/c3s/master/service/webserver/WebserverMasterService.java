package be.webfactor.c3s.master.service.webserver;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import be.webfactor.c3s.controller.PageController;
import be.webfactor.c3s.master.domain.LocationThreadLocal;
import be.webfactor.c3s.master.domain.Page;
import be.webfactor.c3s.master.domain.Template;
import be.webfactor.c3s.master.domain.TemplateEngine;
import be.webfactor.c3s.master.service.MasterService;
import be.webfactor.c3s.master.service.webserver.domain.WebserverSiteConfiguration;
import be.webfactor.c3s.master.service.webserver.domain.WebserverSiteContentRepositoryConnection;
import be.webfactor.c3s.master.service.webserver.domain.WebserverSitePage;
import be.webfactor.c3s.master.service.webserver.domain.WebserverSiteTemplate;
import be.webfactor.c3s.repository.RepositoryConnection;
import be.webfactor.c3s.repository.RepositoryType;

@Service
@Scope("request")
public class WebserverMasterService implements MasterService {

	private static final String CONFIG_FILE = "c3s.json";
	private static final String I18N_BASE_NAME = "i18n";

	private String basePath;
	private WebserverSiteConfiguration config;
	private ResourceBundle resourceBundle;

	public void initialize(RepositoryConnection connection) {
		basePath = connection.getRepositoryId();
		config = new Gson().fromJson(readFile(CONFIG_FILE), WebserverSiteConfiguration.class);

		if (config.getLocationSettings() != null) {
			if (!LocationThreadLocal.hasLocale()) {
				LocationThreadLocal.setLocale(LocaleUtils.toLocale(config.getLocationSettings().getLocale()));
			}
			LocationThreadLocal.setTimeZone(ZoneId.of(config.getLocationSettings().getTimeZone()));
		}

		initResourceBundle();
	}

	private void initResourceBundle() {
		Locale locale = LocationThreadLocal.getLocale();
		URL i18nFolder = getURL(I18N_BASE_NAME + "/");
		ClassLoader classLoader = new URLClassLoader(new URL[] {i18nFolder});

		try {
			resourceBundle = ResourceBundle.getBundle(I18N_BASE_NAME, locale, classLoader);
		} catch (MissingResourceException e) {

		}
	}

	public String getSiteName() {
		return config.getName();
	}

	public List<Page> getPages() {
		return config.getPages().stream().filter(page -> !page.isHidden()).map(pageMapper(false)).collect(Collectors.toList());
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

	public Page getPage(String friendlyUrl) {
		return friendlyUrl == null ?
				null :
				config.getAllPages().stream().filter(webserverSitePage -> friendlyUrl.equals(webserverSitePage.getFriendlyUrl())).map(pageMapper(true)).collect(Collectors.toList()).get(0);
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

			if (!withContents) {
				List<Page> children = webserverSitePage.getChildren().stream().filter(page -> !page.isHidden()).map(pageMapper(false)).collect(Collectors.toList());

				return new Page(friendlyUrl, name, children);
			}

			if (webserverSitePage.isTemplated()) {
				Template template = getTemplate(webserverSitePage.getTemplate());
				Map<String, String> inserts = readInserts(webserverSitePage.getInserts());

				return new Page(friendlyUrl, name, template, inserts);
			}

			return new Page(friendlyUrl, name, readFile(webserverSitePage.getContents()));
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

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
}