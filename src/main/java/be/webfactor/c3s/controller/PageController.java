package be.webfactor.c3s.controller;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import be.webfactor.c3s.registry.domain.MasterRepository;
import be.webfactor.c3s.renderer.PageRenderer;
import be.webfactor.c3s.renderer.PageRendererFactory;
import be.webfactor.c3s.master.service.MasterService;
import be.webfactor.c3s.master.service.MasterServiceFactory;
import be.webfactor.c3s.registry.service.RepositoryRegistryFactory;

@RestController
public class PageController {

	public static final String ASSETS_PREFIX = "/assets/";
	private static final TikaConfig TIKA_CONFIG;

	static {
		try {
			TIKA_CONFIG = new TikaConfig();
		} catch (TikaException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Autowired private RepositoryRegistryFactory repositoryRegistryFactory;
	@Autowired private MasterServiceFactory masterServiceFactory;
	@Autowired private PageRendererFactory pageRendererFactory;

	@RequestMapping("/")
	public String index(HttpServletRequest request) {
		MasterService masterService = getMasterService(request);

		return friendlyUrl(masterService.getIndexPage().getFriendlyUrl(), new String[0], masterService);
	}

	@RequestMapping(ASSETS_PREFIX + "**")
	public ResponseEntity<byte[]> asset(HttpServletRequest request) throws IOException {
		String requestUri = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String assetPath = StringUtils.removeStart(requestUri, ASSETS_PREFIX);
		String assetUrl = getMasterService(request).getAssetUrl(assetPath);
		byte[] content = IOUtils.toByteArray(new URL(assetUrl));

		return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS)).contentType(getContentType(content, assetPath)).body(content);
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

	private MediaType getContentType(byte[] content, String assetPath) throws IOException {
		Metadata metadata = new Metadata();
		metadata.set(Metadata.RESOURCE_NAME_KEY, assetPath);

		return MediaType.valueOf(TIKA_CONFIG.getDetector().detect(TikaInputStream.get(content), metadata).toString());
	}

	private MasterService getMasterService(HttpServletRequest request) {
		MasterRepository repository = repositoryRegistryFactory.getRegistry().findMasterRepository(request.getServerName());

		return masterServiceFactory.forRepositoryConnection(repository.getConnection());
	}

	private String friendlyUrl(String friendlyUrl, String[] params, MasterService masterService) {
		PageRenderer pageRenderer = pageRendererFactory.forMasterService(masterService);

		try {
			return pageRenderer.render(masterService.getPage(friendlyUrl), params);
		} catch (Throwable t) {
			return pageRenderer.render(masterService.getErrorPage(), new String[] { ExceptionUtils.getStackTrace(t) });
		}
	}
}