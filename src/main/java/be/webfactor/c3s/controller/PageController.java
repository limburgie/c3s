package be.webfactor.c3s.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.webfactor.c3s.controller.exception.PageNotFoundException;
import be.webfactor.c3s.controller.helper.apm.ApmTrackerService;
import be.webfactor.c3s.controller.helper.asset.Asset;
import be.webfactor.c3s.controller.helper.asset.AssetService;
import be.webfactor.c3s.controller.helper.uri.RequestUri;
import be.webfactor.c3s.controller.helper.uri.RequestUriThreadLocal;
import be.webfactor.c3s.controller.sitemap.SitemapGenerator;
import be.webfactor.c3s.master.domain.LocaleContext;
import be.webfactor.c3s.master.domain.LocationThreadLocal;
import be.webfactor.c3s.shopping.ShoppingCart;
import be.webfactor.c3s.shopping.ShoppingCartService;
import be.webfactor.c3s.form.FormHandler;
import be.webfactor.c3s.form.FormHandlerFactory;
import be.webfactor.c3s.form.FormParams;
import be.webfactor.c3s.master.domain.Form;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import be.webfactor.c3s.content.service.ContentService;
import be.webfactor.c3s.content.service.ContentServiceFactory;
import be.webfactor.c3s.content.service.domain.ContentItem;
import be.webfactor.c3s.registry.domain.MasterRepository;
import be.webfactor.c3s.renderer.PageRenderer;
import be.webfactor.c3s.renderer.PageRendererFactory;
import be.webfactor.c3s.master.service.MasterService;
import be.webfactor.c3s.master.service.MasterServiceFactory;
import be.webfactor.c3s.registry.service.RepositoryRegistryFactory;
import be.webfactor.c3s.repository.RepositoryConnection;

@RestController
@Order(2)
public class PageController {

	public static final String ASSETS_PREFIX = "/assets/";
	private static final String C3S_PREFIX = "/c3s/";
	private static final String SUBMIT_URI = "/submit";
	private static final String EDIT_URL_JS_FILENAME = "c3s-edit-url.js";
	public static final String EDIT_URL_JS_PATH = C3S_PREFIX + EDIT_URL_JS_FILENAME;
	public static final String SITEMAP_PATH = "/sitemap.xml";

	@Autowired private RepositoryRegistryFactory repositoryRegistryFactory;
	@Autowired private MasterServiceFactory masterServiceFactory;
	@Autowired private PageRendererFactory pageRendererFactory;
	@Autowired private FormHandlerFactory formHandlerFactory;
	@Autowired private ContentServiceFactory contentServiceFactory;
	@Autowired private SitemapGenerator sitemapGenerator;
	@Autowired private ApmTrackerService apmTrackerService;
	@Autowired private ShoppingCartService shoppingCartService;
	@Autowired private AssetService assetService;

	@RequestMapping(ASSETS_PREFIX + "**")
	public ResponseEntity<byte[]> asset(HttpServletRequest request) throws IOException {
        String basePath = getMasterService(request).getBaseUrl();
		Asset asset = assetService.getAsset(request, basePath);

		return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS)).contentType(asset.getContentType()).body(asset.getData());
	}

	@RequestMapping(value = SUBMIT_URI, method = RequestMethod.POST)
	public void submitForm(HttpServletRequest request, HttpServletResponse response, @CookieValue(value = ShoppingCart.COOKIE_NAME, required = false) String shoppingCartEncoded) {
		apmTrackerService.setTransactionName(request);
		shoppingCartService.initializeShoppingCart(shoppingCartEncoded);

		MasterService masterService = getMasterService(request);
		FormHandler formHandler = formHandlerFactory.forMasterService(masterService);
		Form form = masterService.getForm(request.getParameter("form"));

		formHandler.handleForm(form, new FormParams(request));

		String referer = request.getParameter("referer");
		response.setHeader("Location", referer == null ? "/" : referer);
		response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	}

	@RequestMapping(EDIT_URL_JS_PATH)
	public ResponseEntity<byte[]> editUrlJavascript() throws IOException {
		byte[] content = IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream(EDIT_URL_JS_FILENAME));

		return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).contentType(MediaType.valueOf("application/javascript")).body(content);
	}

	@RequestMapping(SITEMAP_PATH)
	public ResponseEntity<String> sitemap(HttpServletRequest request) throws MalformedURLException {
		MasterService masterService = getMasterService(request);
		String sitemapXml = sitemapGenerator.generate(request, masterService);

		return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).contentType(MediaType.TEXT_XML).body(sitemapXml);
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
	public String friendlyUrl(HttpServletRequest request, HttpServletResponse response, @CookieValue(value = ShoppingCart.COOKIE_NAME, required = false) String shoppingCartEncoded) {
		apmTrackerService.setTransactionName(request);
		shoppingCartService.initializeShoppingCart(shoppingCartEncoded);

		MasterService masterService = getMasterService(request);
		RequestUri requestUri = new RequestUri(request, masterService);

		LocaleContext localeContext = requestUri.getLocaleContext();

		if (isLocale(requestUri.getFriendlyUrl())) {
			return errorPage(masterService, null, response, HttpServletResponse.SC_NOT_FOUND);
		}

		if (!localeContext.isUriLocalePrefixed() && masterService.getLocales().size() > 1) {
			String language = getBestMatchingLanguage(request, masterService.getLocales());
			response.setHeader("Location", "/" + language + "/" + requestUri.getRedirectUrl());
			response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
			return null;
		}

		RequestUriThreadLocal.setCurrentUri(requestUri.getPathWithoutLocalePrefix());
		LocationThreadLocal.setLocaleContext(localeContext);
		return friendlyUrl(requestUri.getFriendlyUrl(), requestUri.getParams(), masterService, response);
	}

	private boolean isLocale(String friendlyUrl) {
		return Arrays.stream(Locale.getAvailableLocales()).map(Locale::getLanguage).distinct().anyMatch(lang -> lang.equals(friendlyUrl));
	}

	private String getBestMatchingLanguage(HttpServletRequest request, List<Locale> siteLocales) {
		List<String> siteLanguages = siteLocales.stream().map(Locale::getLanguage).distinct().collect(Collectors.toList());
		return Collections.list(request.getLocales()).stream()
				.filter(locale -> siteLanguages.contains(locale.getLanguage()))
				.findFirst().orElse(siteLocales.get(0)).getLanguage();
	}

	private MasterService getMasterService(HttpServletRequest request) {
		MasterRepository repository = repositoryRegistryFactory.getRegistry().findMasterRepository(request.getServerName());

		return masterServiceFactory.forRepositoryConnection(repository.getConnection());
	}

	private String friendlyUrl(String friendlyUrl, String[] params, MasterService masterService, HttpServletResponse response) {
		PageRenderer pageRenderer = pageRendererFactory.forMasterService(masterService);

		try {
			return pageRenderer.render(masterService.getPage(friendlyUrl), params);
		} catch (PageNotFoundException e) {
			return errorPage(masterService, e, response, HttpServletResponse.SC_NOT_FOUND);
		} catch (Throwable e) {
			return errorPage(masterService, e, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private String errorPage(MasterService masterService, Throwable exception, HttpServletResponse response, int status) {
		response.setStatus(status);
		try {
			return pageRendererFactory.forMasterService(masterService).render(
					masterService.getErrorPage(), exception == null ? new String[] { "Page not found" } : new String[] { ExceptionUtils.getStackTrace(exception) });
		} catch (Throwable t) {
			LogFactory.getLog(PageController.class).error(t);
			return null;
		}
	}
}