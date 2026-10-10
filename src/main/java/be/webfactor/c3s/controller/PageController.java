package be.webfactor.c3s.controller;

import be.webfactor.c3s.contentrepository.ContentRepository;
import be.webfactor.c3s.contentrepository.ContentRepositoryConnection;
import be.webfactor.c3s.contentrepository.ContentRepositoryFactory;
import be.webfactor.c3s.contentrepository.domain.ContentItem;
import be.webfactor.c3s.controller.exception.PageNotFoundException;
import be.webfactor.c3s.controller.helper.site.SiteAssetStoreResolver;
import be.webfactor.c3s.controller.helper.uri.RequestUri;
import be.webfactor.c3s.controller.helper.uri.RequestUriThreadLocal;
import be.webfactor.c3s.renderer.PageRenderer;
import be.webfactor.c3s.renderer.PageRendererFactory;
import be.webfactor.c3s.shopping.ShoppingCart;
import be.webfactor.c3s.shopping.ShoppingCartService;
import be.webfactor.c3s.siteassetstore.SiteAssetStore;
import be.webfactor.c3s.siteassetstore.domain.LocaleContext;
import be.webfactor.c3s.siteassetstore.domain.LocationThreadLocal;
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

	private static final String C3S_PREFIX = "/c3s/";
	private static final String EDIT_URL_JS_FILENAME = "c3s-edit-url.js";
	public static final String EDIT_URL_JS_PATH = C3S_PREFIX + EDIT_URL_JS_FILENAME;

	private final SiteAssetStoreResolver siteAssetStoreResolver;
	private final PageRendererFactory pageRendererFactory;
	private final ContentRepositoryFactory contentRepositoryFactory;
	private final ShoppingCartService shoppingCartService;

	@GetMapping(EDIT_URL_JS_PATH)
	public ResponseEntity<byte[]> editUrlJavascript() throws IOException {
		try (InputStream jsResource = getClass().getClassLoader().getResourceAsStream(EDIT_URL_JS_FILENAME)) {
			if (jsResource == null) {
				return ResponseEntity.notFound().build();
			}

			return ResponseEntity.ok()
					.cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
					.contentType(MediaType.valueOf("application/javascript"))
					.body(IOUtils.toByteArray(jsResource));
		}
	}

	@GetMapping(C3S_PREFIX + "**")
	public void editUrl(HttpServletRequest request, HttpServletResponse response) {
		String requestUri = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String contentItemId = StringUtils.removeStart(requestUri, C3S_PREFIX);

		ContentRepositoryConnection repoConnection = siteAssetStoreResolver.forRequest(request).getContentRepositoryConnection();
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

		SiteAssetStore siteAssetStore = siteAssetStoreResolver.forRequest(request);
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