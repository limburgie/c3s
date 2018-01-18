package be.webfactor.c3s.master.service.webserver;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import be.webfactor.c3s.controller.PageController;
import be.webfactor.c3s.master.domain.Page;
import be.webfactor.c3s.master.domain.Site;
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

	private String basePath;
	private WebserverSiteConfiguration config;

	public void initialize(RepositoryConnection connection) {
		basePath = connection.getRepositoryId();
		config = new Gson().fromJson(readFile(CONFIG_FILE), WebserverSiteConfiguration.class);
	}

	public Site getSite() {
		String name = config.getName();
		Page indexPage = getPage(config.getIndexPage());
		String errorTemplate = readFile(config.getErrorTemplateFile());
		TemplateEngine templateEngine = TemplateEngine.valueOf(config.getTemplateEngine());
		String template = readFile(config.getTemplateFile());

		WebserverSiteContentRepositoryConnection repositoryConnection = config.getContentRepositoryConnection();
		RepositoryType contentRepositoryType = RepositoryType.valueOf(repositoryConnection.getType());
		String contentRepositoryId = repositoryConnection.getRepositoryId();
		String contentRepositoryAccessToken = repositoryConnection.getAccessToken();

		return new Site(name, indexPage, errorTemplate, templateEngine, template, new RepositoryConnection(contentRepositoryType, contentRepositoryId, contentRepositoryAccessToken));
	}

	public List<Page> getPages() {
		return config.getPages().stream().map(pageMapper()).collect(Collectors.toList());
	}

	public Page getPage(String friendlyUrl) {
		return friendlyUrl == null ? null : config.getAllPages().stream()
				.filter(webserverSitePage -> friendlyUrl.equals(webserverSitePage.getFriendlyUrl()))
				.map(pageMapper()).collect(Collectors.toList()).get(0);
	}

	private Function<WebserverSitePage, Page> pageMapper() {
		return webserverSitePage -> {
			String friendlyUrl = webserverSitePage.getFriendlyUrl();
			String name = webserverSitePage.getName();
			String template = readFile(webserverSitePage.getTemplateFile());

			List<Page> children = webserverSitePage.getChildren().stream().map(pageMapper()).collect(Collectors.toList());

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
}
