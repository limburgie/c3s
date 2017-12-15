package be.webfactor.c3s.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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

		return friendlyUrl(masterService.getSite().getIndexPage().getFriendlyUrl(), masterService);
	}

	@RequestMapping("/assets/**")
	public void asset(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String requestUri = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String assetPath = StringUtils.removeStart(requestUri, ASSETS_PREFIX);
		String assetUrl = getMasterService(request).getAssetUrl(assetPath);

		try(InputStream is = new URL(assetUrl).openStream()) {
			IOUtils.copy(is, response.getOutputStream());
			response.flushBuffer();
		}
	}

	@RequestMapping("/{friendlyUrl}")
	public String friendlyUrl(@PathVariable("friendlyUrl") final String friendlyUrl, HttpServletRequest request) {
		return friendlyUrl(friendlyUrl, getMasterService(request));
	}

	private MasterService getMasterService(HttpServletRequest request) {
		MasterRepository repository = repositoryRegistryFactory.getRegistry().findMasterRepository(request.getServerName());

		return masterServiceFactory.forRepositoryConnection(repository.getConnection());
	}

	private String friendlyUrl(String friendlyUrl, MasterService masterService) {
		return pageRendererFactory.forMasterService(masterService).render(masterService.getPage(friendlyUrl));
	}
}
