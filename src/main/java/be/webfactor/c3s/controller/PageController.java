package be.webfactor.c3s.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import be.webfactor.c3s.registry.domain.MasterRepository;
import be.webfactor.c3s.renderer.PageRendererFactory;
import be.webfactor.c3s.master.service.MasterService;
import be.webfactor.c3s.master.service.MasterServiceFactory;
import be.webfactor.c3s.registry.service.RepositoryRegistryFactory;

@RestController
public class PageController {

	public static final String ASSETS_PREFIX = "/assets/";

	@Autowired private RepositoryRegistryFactory repositoryRegistryFactory;
	@Autowired private MasterServiceFactory masterServiceFactory;
	@Autowired private PageRendererFactory pageRendererFactory;

	@RequestMapping("/")
	public String index(HttpServletRequest request) {
		MasterService masterService = getMasterService(request);

		return friendlyUrl(masterService.getSite().getIndexPage().getFriendlyUrl(), new String[0], masterService);
	}

	@RequestMapping("/assets/**")
	public void asset(HttpServletRequest request, HttpServletResponse response) throws IOException, URISyntaxException {
		String requestUri = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String assetPath = StringUtils.removeStart(requestUri, ASSETS_PREFIX);
		String assetUrl = getMasterService(request).getAssetUrl(assetPath);

		URL url = new URL(assetUrl);

		try(InputStream is = url.openStream()) {
			IOUtils.copy(is, response.getOutputStream());
			response.setContentType(Files.probeContentType(Paths.get(url.toURI())));
			response.flushBuffer();
		}
	}

	@RequestMapping("/**")
	public String friendlyUrl(HttpServletRequest request) {
		String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String friendlyUrl = path.substring(1);
		String[] params = new String[0];

		if (friendlyUrl.contains("/")) {
			String[] pathParts = friendlyUrl.split("/", 2);

			friendlyUrl = pathParts[0];
			params = pathParts[1].split("/");
		}

		return friendlyUrl(friendlyUrl, params, getMasterService(request));
	}

	private MasterService getMasterService(HttpServletRequest request) {
		MasterRepository repository = repositoryRegistryFactory.getRegistry().findMasterRepository(request.getServerName());

		return masterServiceFactory.forRepositoryConnection(repository.getConnection());
	}

	private String friendlyUrl(String friendlyUrl, String[] params, MasterService masterService) {
		return pageRendererFactory.forMasterService(masterService).render(masterService.getPage(friendlyUrl), params);
	}
}
