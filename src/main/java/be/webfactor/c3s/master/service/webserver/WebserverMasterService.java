package be.webfactor.c3s.master.service.webserver;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import be.webfactor.c3s.controller.PageController;
import be.webfactor.c3s.master.domain.Page;
import be.webfactor.c3s.master.domain.TemplateEngine;
import be.webfactor.c3s.master.service.MasterService;
import be.webfactor.c3s.master.service.webserver.domain.WebserverSiteConfiguration;
import be.webfactor.c3s.master.service.webserver.domain.WebserverSiteContentRepositoryConnection;
import be.webfactor.c3s.master.service.webserver.domain.WebserverSitePage;
import be.webfactor.c3s.repository.RepositoryConnection;
import be.webfactor.c3s.repository.RepositoryType;

@Service
@Scope("request")
public class WebserverMasterService implements MasterService {

	private static final String CONFIG_FILE = "c3s.json";
	private static final String ERROR_PAGE_FRIENDLY_URL = "error";
	private static final String ERROR_PAGE_NAME = "Error";

	private String basePath;
	private WebserverSiteConfiguration config;

	public void initialize(RepositoryConnection connection) {
		basePath = connection.getRepositoryId();
		config = new Gson().fromJson(readFile(CONFIG_FILE), WebserverSiteConfiguration.class);
	}

	public List<Page> getPages() {
		return config.getPages().stream().map(pageMapper(false)).collect(Collectors.toList());
	}

	public TemplateEngine getTemplateEngine() {
		return TemplateEngine.valueOf(config.getTemplateEngine());
	}

	public RepositoryConnection getRepositoryConnection() {
		WebserverSiteContentRepositoryConnection repositoryConnection = config.getContentRepositoryConnection();

		RepositoryType type = RepositoryType.valueOf(repositoryConnection.getType());
		String id = repositoryConnection.getRepositoryId();
		String accessToken = repositoryConnection.getAccessToken();

		return new RepositoryConnection(type, id, accessToken);
	}

	public Page getPage(String friendlyUrl) {
		return friendlyUrl == null ? null : config.getAllPages().stream()
				.filter(webserverSitePage -> friendlyUrl.equals(webserverSitePage.getFriendlyUrl()))
				.map(pageMapper(true)).collect(Collectors.toList()).get(0);
	}

	public Page getIndexPage() {
		return getPage(config.getIndexPage());
	}

	public Page getErrorPage() {
		;return new Page(ERROR_PAGE_FRIENDLY_URL, ERROR_PAGE_NAME, readFile(config.getErrorTemplateFile()));
	}

	private Function<WebserverSitePage, Page> pageMapper(boolean withContents) {
		return webserverSitePage -> {
			String friendlyUrl = webserverSitePage.getFriendlyUrl();
			String name = webserverSitePage.getName();
			String template = withContents ? readFile(webserverSitePage.getTemplateFile()) : null;

			List<Page> children = webserverSitePage.getChildren().stream().map(pageMapper(false)).collect(Collectors.toList());

			return new Page(friendlyUrl, name, template, children);
		};
	}

	public String getAssetUrl(String assetPath) {
		return basePath + PageController.ASSETS_PREFIX + assetPath;
	}

	private String readFile(String path) {
		try {
			return IOUtils.toString(new URI(basePath + "/" + path));
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public RepositoryType getType() {
		return RepositoryType.WEB_SERVER;
	}

	public String getSiteTemplate() {
		return readFile(config.getTemplateFile());
	}
}
