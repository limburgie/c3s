package be.webfactor.c3s.controller;

import be.webfactor.c3s.contentrepository.ContentRepository;
import be.webfactor.c3s.contentrepository.ContentRepositoryConnection;
import be.webfactor.c3s.contentrepository.ContentRepositoryFactory;
import be.webfactor.c3s.contentrepository.domain.ContentItem;
import be.webfactor.c3s.controller.exception.PageNotFoundException;
import be.webfactor.c3s.controller.helper.asset.Asset;
import be.webfactor.c3s.controller.helper.asset.AssetService;
import be.webfactor.c3s.controller.helper.uri.RequestUri;
import be.webfactor.c3s.controller.helper.uri.RequestUriThreadLocal;
import be.webfactor.c3s.controller.sitemap.SitemapBuilder;
import be.webfactor.c3s.form.FormHandler;
import be.webfactor.c3s.form.FormHandlerFactory;
import be.webfactor.c3s.form.FormParams;
import be.webfactor.c3s.renderer.PageRenderer;
import be.webfactor.c3s.renderer.PageRendererFactory;
import be.webfactor.c3s.shopping.ShoppingCart;
import be.webfactor.c3s.shopping.ShoppingCartService;
import be.webfactor.c3s.siteassetstore.SiteAssetStore;
import be.webfactor.c3s.siteassetstore.SiteAssetStoreFactory;
import be.webfactor.c3s.siteassetstore.domain.Form;
import be.webfactor.c3s.siteassetstore.domain.LocaleContext;
import be.webfactor.c3s.siteassetstore.domain.LocationThreadLocal;
import be.webfactor.c3s.siteconnectionregistry.domain.SiteConnection;
import be.webfactor.c3s.siteconnectionregistry.SiteConnectionRegistryFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Slf4j
@Order(2)
@RestController
@RequiredArgsConstructor
public class PageController {

	public static final String ASSETS_PREFIX = "/assets/";
	private static final String C3S_PREFIX = "/c3s/";
	private static final String SUBMIT_URI = "/submit";
	private static final String EDIT_URL_JS_FILENAME = "c3s-edit-url.js";
	public static final String EDIT_URL_JS_PATH = C3S_PREFIX + EDIT_URL_JS_FILENAME;
	public static final String SITEMAP_PATH = "/sitemap.xml";
	public static final String FAVICON_FOLDER = ASSETS_PREFIX + "img/favicon";
	public static final String FAVICON_ICO_FILENAME = "favicon.ico";
	public static final String FAVICON_SVG_FILENAME = "favicon.svg";

	private final SiteConnectionRegistryFactory siteConnectionRegistryFactory;
	private final SiteAssetStoreFactory siteAssetStoreFactory;
	private final PageRendererFactory pageRendererFactory;
	private final FormHandlerFactory formHandlerFactory;
	private final ContentRepositoryFactory contentRepositoryFactory;
	private final SitemapBuilder sitemapBuilder;
	private final ShoppingCartService shoppingCartService;
	private final AssetService assetService;

	@GetMapping("/" + FAVICON_ICO_FILENAME)
	public ResponseEntity<byte[]> faviconIco(HttpServletRequest request) {
		return favicon(request, FAVICON_ICO_FILENAME, "image/x-icon");
	}

	@GetMapping("/" + FAVICON_SVG_FILENAME)
	public ResponseEntity<byte[]> faviconSvg(HttpServletRequest request) {
		return favicon(request, FAVICON_SVG_FILENAME, "image/svg+xml");
	}

	private ResponseEntity<byte[]> favicon(HttpServletRequest request, String fileName, String mediaType) {
		byte[] data = getSiteAssetStore(request).readAsset(FAVICON_FOLDER + "/" + fileName);
		return ResponseEntity.ok()
				.cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
				.contentType(MediaType.parseMediaType(mediaType))
				.body(data);
	}

	@GetMapping(ASSETS_PREFIX + "**")
	public ResponseEntity<byte[]> asset(HttpServletRequest request) throws IOException {
		Asset asset = assetService.getAsset(request, getSiteAssetStore(request));

		return ResponseEntity.ok()
				.cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
				.contentType(asset.contentType())
				.body(asset.data());
	}

	@PostMapping(value = SUBMIT_URI)
	public void submitForm(HttpServletRequest request, HttpServletResponse response, @CookieValue(value = ShoppingCart.COOKIE_NAME, required = false) String shoppingCartEncoded) {
		shoppingCartService.initializeShoppingCart(shoppingCartEncoded);

		SiteAssetStore siteAssetStore = getSiteAssetStore(request);

		String locale = request.getParameter("locale");
		if (locale != null) {
			siteAssetStore.getLocales().stream().filter(l -> l.toString().equals(locale)).findFirst()
					.ifPresent(l -> LocationThreadLocal.setLocaleContext(new LocaleContext(l)));
		}

		FormHandler formHandler = formHandlerFactory.forSiteAssetStore(siteAssetStore);
		Form form = siteAssetStore.getForm(request.getParameter("form"));

		formHandler.handleForm(form, new FormParams(request));

		String referer = request.getParameter("referer");
		response.setHeader("Location", referer == null ? "/" : referer);
		response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	}

	@GetMapping(EDIT_URL_JS_PATH)
	public ResponseEntity<byte[]> editUrlJavascript() throws IOException {
		InputStream jsResource = getClass().getClassLoader().getResourceAsStream(EDIT_URL_JS_FILENAME);

		if (jsResource == null) {
			return ResponseEntity.notFound().build();
		}

        return ResponseEntity.ok()
				.cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
				.contentType(MediaType.valueOf("application/javascript"))
				.body(IOUtils.toByteArray(jsResource));
	}

	@GetMapping(value = SITEMAP_PATH, produces = MediaType.TEXT_XML_VALUE)
	public ResponseEntity<String> sitemap(HttpServletRequest request) throws MalformedURLException {
		SiteAssetStore siteAssetStore = getSiteAssetStore(request);
		String sitemapXml = sitemapBuilder.generate(request, siteAssetStore);

		return ResponseEntity.ok()
				.cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
				.contentType(MediaType.TEXT_XML)
				.body(sitemapXml);
	}

	@GetMapping(C3S_PREFIX + "**")
	public void editUrl(HttpServletRequest request, HttpServletResponse response) {
		String requestUri = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String contentItemId = StringUtils.removeStart(requestUri, C3S_PREFIX);

		ContentRepositoryConnection repoConnection = getSiteAssetStore(request).getContentRepositoryConnection();
		ContentRepository contentRepository = repoConnection == null ? null : contentRepositoryFactory.forConnection(repoConnection);

		if (contentRepository != null) {
			ContentItem contentItem = contentRepository.getApi().findById(contentItemId);

			if (contentItem != null) {
				response.setHeader(HttpHeaders.LOCATION, contentItem.getEditUrl());
				response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
			}
		}
	}

	@GetMapping("/**")
	public String friendlyUrl(HttpServletRequest request, HttpServletResponse response, @CookieValue(value = ShoppingCart.COOKIE_NAME, required = false) String shoppingCartEncoded) {
		shoppingCartService.initializeShoppingCart(shoppingCartEncoded);

		SiteAssetStore siteAssetStore = getSiteAssetStore(request);
		RequestUri requestUri = new RequestUri(request, siteAssetStore);

		LocaleContext localeContext = requestUri.getLocaleContext();

		if (isLocale(requestUri.getFriendlyUrl())) {
			return errorPage(siteAssetStore, null, response, HttpServletResponse.SC_NOT_FOUND);
		}

		if (!localeContext.uriLocalePrefixed() && siteAssetStore.getLocales().size() > 1) {
			String language = getBestMatchingLanguage(request, siteAssetStore.getLocales());
			response.setHeader("Location", "/" + language + "/" + requestUri.getRedirectUrl());
			response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
			return null;
		}

		RequestUriThreadLocal.setCurrentUri(requestUri.getPathWithoutLocalePrefix());
		LocationThreadLocal.setLocaleContext(localeContext);
		return friendlyUrl(requestUri.getFriendlyUrl(), requestUri.getParams(), siteAssetStore, response);
	}

	private boolean isLocale(String friendlyUrl) {
		return Arrays.stream(Locale.getAvailableLocales()).map(Locale::getLanguage).distinct().anyMatch(lang -> lang.equals(friendlyUrl));
	}

	private String getBestMatchingLanguage(HttpServletRequest request, List<Locale> siteLocales) {
		List<String> siteLanguages = siteLocales.stream().map(Locale::getLanguage).distinct().toList();

		return Collections.list(request.getLocales()).stream()
				.filter(locale -> siteLanguages.contains(locale.getLanguage()))
				.findFirst().orElse(siteLocales.get(0)).getLanguage();
	}

	private SiteAssetStore getSiteAssetStore(HttpServletRequest request) {
		SiteConnection repository = siteConnectionRegistryFactory.getRegistry().getConnectionForHost(request.getServerName());

		return siteAssetStoreFactory.forConnection(repository.connection());
	}

	private String friendlyUrl(String friendlyUrl, String[] params, SiteAssetStore siteAssetStore, HttpServletResponse response) {
		PageRenderer pageRenderer = pageRendererFactory.forSiteAssetStore(siteAssetStore);

		try {
			return pageRenderer.render(siteAssetStore.getPage(friendlyUrl), params);
		} catch (PageNotFoundException e) {
			return errorPage(siteAssetStore, e, response, HttpServletResponse.SC_NOT_FOUND);
		} catch (Throwable e) {
			return errorPage(siteAssetStore, e, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private String errorPage(SiteAssetStore siteAssetStore, Throwable exception, HttpServletResponse response, int status) {
		String errorParam = exception == null ? "Page not found" : ExceptionUtils.getStackTrace(exception);
		response.setStatus(status);
		try {
			return pageRendererFactory.forSiteAssetStore(siteAssetStore).render(siteAssetStore.getErrorPage(), new String[] { errorParam });
		} catch (Throwable t) {
			LogFactory.getLog(PageController.class).error(t);
			return null;
		}
	}
}