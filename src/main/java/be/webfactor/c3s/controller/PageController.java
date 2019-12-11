package be.webfactor.c3s.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.glowroot.agent.api.Glowroot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import be.webfactor.c3s.content.service.ContentService;
import be.webfactor.c3s.content.service.ContentServiceFactory;
import be.webfactor.c3s.content.service.domain.ContentItem;
import be.webfactor.c3s.controller.sass.SassCompiler;
import be.webfactor.c3s.master.domain.LocationThreadLocal;
import be.webfactor.c3s.registry.domain.MasterRepository;
import be.webfactor.c3s.renderer.PageRenderer;
import be.webfactor.c3s.renderer.PageRendererFactory;
import be.webfactor.c3s.master.service.MasterService;
import be.webfactor.c3s.master.service.MasterServiceFactory;
import be.webfactor.c3s.registry.service.RepositoryRegistryFactory;
import be.webfactor.c3s.repository.RepositoryConnection;

@RestController
public class PageController {

	public static final String ASSETS_PREFIX = "/assets/";
	private static final String C3S_PREFIX = "/c3s/";
	private static final String LANG_PREFIX = "/lang/";
	private static final String LOCALE_COOKIE_NAME = "C3S_LOCALE";
	private static final String EDIT_URL_JS_FILENAME = "c3s-edit-url.js";
	public static final String EDIT_URL_JS_PATH = C3S_PREFIX + EDIT_URL_JS_FILENAME;
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
	@Autowired private ContentServiceFactory contentServiceFactory;

	@RequestMapping("/")
	public String index(HttpServletRequest request, @CookieValue(value = LOCALE_COOKIE_NAME, required = false) String locale) {
		setTransactionName(request);

		LocationThreadLocal.setLocale(LocaleUtils.toLocale(locale));

		return friendlyUrl(getMasterService(request).getIndexPage().getFriendlyUrl(), new String[0], getMasterService(request));
	}

	@RequestMapping(ASSETS_PREFIX + "**")
	public ResponseEntity<byte[]> asset(HttpServletRequest request) throws IOException {
		setTransactionName(request);

		String requestUri = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String assetPath = StringUtils.removeStart(requestUri, ASSETS_PREFIX);
		MasterService masterService = getMasterService(request);
		String basePath = masterService.getBaseUrl();
		byte[] content = getAssetBytes(basePath, assetPath);

		return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS)).contentType(getContentType(content, assetPath)).body(content);
	}

	private byte[] getAssetBytes(String basePath, String assetPath) throws IOException {
		String assetUrl = basePath + ASSETS_PREFIX + assetPath;
		try {
			return IOUtils.toByteArray(new URL(assetUrl));
		} catch(IOException e) {
			if (assetPath.endsWith(".css")) {
				String relativeDirectory = assetPath.substring(0, assetPath.lastIndexOf("/") + 1);
				String sassAssetPath = assetPath.replace(".css", ".scss");

				SassCompiler sassCompiler = new SassCompiler(basePath, relativeDirectory);

				return sassCompiler.compile(IOUtils.toByteArray(new URL(basePath + ASSETS_PREFIX + sassAssetPath)));
			} else {
				throw e;
			}
		}
	}

	@RequestMapping(EDIT_URL_JS_PATH)
	public ResponseEntity<byte[]> editUrlJavascript() throws IOException {
		byte[] content = IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream(EDIT_URL_JS_FILENAME));

		return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).contentType(MediaType.valueOf("application/javascript")).body(content);
	}

	@RequestMapping(LANG_PREFIX + "**")
	public void changeLanguageUrl(HttpServletRequest request, HttpServletResponse response) {
		String requestUri = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String newLocale = StringUtils.removeStart(requestUri, LANG_PREFIX);

		response.addCookie(createLocaleCookie(newLocale));
		response.setHeader("Location", getReferer(request));
		response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	}

	private String getReferer(HttpServletRequest request) {
		String referer = request.getHeader("Referer");

		if (referer == null) {
			String scheme = request.getScheme();
			String host = request.getHeader("Host");

			referer = scheme + "://" + host;
		}

		return referer;
	}

	private Cookie createLocaleCookie(String newLocale) {
		Cookie cookie = new Cookie(LOCALE_COOKIE_NAME, newLocale);

		cookie.setMaxAge(Integer.MAX_VALUE);
		cookie.setHttpOnly(true);
		cookie.setPath("/");

		return cookie;
	}

	@RequestMapping(C3S_PREFIX + "**")
	public void editUrl(HttpServletRequest request, HttpServletResponse response) {
		String requestUri = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String contentItemId = StringUtils.removeStart(requestUri, C3S_PREFIX);

		RepositoryConnection repoConnection = getMasterService(request).getRepositoryConnection();
		ContentService contentService = repoConnection == null ? null : contentServiceFactory.forRepositoryConnection(repoConnection);

		if (contentService != null) {
			ContentItem contentItem = contentService.getApi().findById(contentItemId);

			if (contentItem != null) {
				response.setHeader("Location", contentItem.getEditUrl());
				response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
			}
		}
	}

	@RequestMapping("/**")
	public String friendlyUrl(HttpServletRequest request, @CookieValue(value = LOCALE_COOKIE_NAME, required = false) String locale) {
		setTransactionName(request);
		LocationThreadLocal.setLocale(LocaleUtils.toLocale(locale));

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

	private void setTransactionName(HttpServletRequest request) {
		Glowroot.setTransactionName(request.getRequestURL().toString());
	}
}