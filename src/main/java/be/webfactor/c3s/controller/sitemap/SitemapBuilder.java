package be.webfactor.c3s.controller.sitemap;

import be.webfactor.c3s.contentrepository.ContentRepositoryConnection;
import be.webfactor.c3s.contentrepository.ContentRepositoryFactory;
import be.webfactor.c3s.contentrepository.domain.ContentApi;
import be.webfactor.c3s.contentrepository.domain.ContentItem;
import be.webfactor.c3s.controller.SeoController;
import be.webfactor.c3s.siteassetstore.domain.LocaleContext;
import be.webfactor.c3s.siteassetstore.domain.LocationThreadLocal;
import be.webfactor.c3s.siteassetstore.domain.Page;
import be.webfactor.c3s.siteassetstore.SiteAssetStore;
import cz.jiripinkas.jsitemapgenerator.WebPage;
import cz.jiripinkas.jsitemapgenerator.generator.SitemapGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SitemapBuilder {

	private final ContentRepositoryFactory contentRepositoryFactory;

	public Sitemap generate(HttpServletRequest request, SiteAssetStore siteAssetStore) throws MalformedURLException {
		List<Locale> locales = siteAssetStore.getLocales();
		List<Page> pages = getNonIndexPages(siteAssetStore);
		Map<String, Map<Locale, Instant>> detailPaths = new LinkedHashMap<>();
		boolean complete = findDetailPaths(siteAssetStore, pages, locales, detailPaths);

		String xml = SitemapGenerator.of(getBaseUrl(request)).addPages(createWebPages(locales, pages, detailPaths)).toString();
		return new Sitemap(xml, complete);
	}

	private List<WebPage> createWebPages(List<Locale> locales, List<Page> pages, Map<String, Map<Locale, Instant>> detailPaths) {
		List<WebPage> webPages = new ArrayList<>();

		if (locales.isEmpty()) {
			webPages.add(WebPage.builder().nameRoot().build());
			for (Page page : pages) {
				webPages.add(WebPage.builder().name(page.getFriendlyUrl()).build());
			}
			detailPaths.forEach((detailPath, lastModifiedByLocale) -> {
				WebPage.WebPageBuilder webPageBuilder = WebPage.builder().name(detailPath);
				addLastModified(webPageBuilder, lastModifiedByLocale.get(null));
				webPages.add(webPageBuilder.build());
			});
		} else {
			for (Locale locale : locales) {
				webPages.add(buildWebPage("", locale, locales, null));
				for (Page page : pages) {
					webPages.add(buildWebPage(page.getFriendlyUrl(), locale, locales, null));
				}
				detailPaths.forEach((detailPath, lastModifiedByLocale) -> {
					if (lastModifiedByLocale.containsKey(locale)) {
						List<Locale> availableLocales = new ArrayList<>(lastModifiedByLocale.keySet());
						webPages.add(buildWebPage(detailPath, locale, availableLocales, lastModifiedByLocale.get(locale)));
					}
				});
			}
		}

		return webPages;
	}

	/**
	 * Returns all visible pages, including nested child pages, except the index page: that one is served at the
	 * root URL, which the sitemap always lists separately.
	 */
	private List<Page> getNonIndexPages(SiteAssetStore siteAssetStore) {
		Page indexPage = siteAssetStore.getIndexPage();
		String indexFriendlyUrl = indexPage == null ? null : indexPage.getFriendlyUrl();

		List<Page> pages = new ArrayList<>();
		addWithDescendants(siteAssetStore.getPages(false), pages);
		pages.removeIf(page -> page.getFriendlyUrl().equals(indexFriendlyUrl));

		return pages;
	}

	private void addWithDescendants(List<Page> pages, List<Page> result) {
		for (Page page : pages) {
			result.add(page);
			if (page.getChildren() != null) {
				addWithDescendants(page.getChildren(), result);
			}
		}
	}

	/**
	 * Adds the paths of the detail pages served by pages with sitemap settings ({friendlyUrl}/{param value}),
	 * each with the locales in which the content repository returned the item, mapped to the item's last modified date
	 * in that locale (null if unknown). A non-localized site is queried once with the default locale, which is stored
	 * as the null key.
	 * <p>
	 * Content backends fix the language from {@link LocationThreadLocal}, so the content repository is initialized
	 * again for every locale. Failures are logged and skipped, so the static pages are always listed.
	 *
	 * @return false if the content repository failed to return the items of a page in a locale
	 */
	private boolean findDetailPaths(SiteAssetStore siteAssetStore, List<Page> pages, List<Locale> locales, Map<String, Map<Locale, Instant>> detailPaths) {
		List<Page> detailPages = pages.stream().filter(page -> page.getSitemap() != null).filter(this::isValid).toList();
		ContentRepositoryConnection connection = siteAssetStore.getContentRepositoryConnection();

		if (detailPages.isEmpty() || connection == null) {
			return true;
		}

		LocaleContext originalLocaleContext = LocationThreadLocal.getLocaleContext();
		boolean complete = true;

		try {
			if (locales.isEmpty()) {
				LocationThreadLocal.setLocaleContext(new LocaleContext());
				complete = addDetailPaths(connection, detailPages, null, detailPaths);
			} else {
				for (Locale locale : locales) {
					LocationThreadLocal.setLocaleContext(new LocaleContext(locale, false, true));
					complete &= addDetailPaths(connection, detailPages, locale, detailPaths);
				}
			}
		} finally {
			LocationThreadLocal.setLocaleContext(originalLocaleContext);
		}

		return complete;
	}

	private boolean isValid(Page page) {
		try {
			page.getSitemap().validate();
			return true;
		} catch (IllegalArgumentException e) {
			log.warn("Skipping detail pages of page '{}' in sitemap: {}", page.getFriendlyUrl(), e.getMessage());
			return false;
		}
	}

	private boolean addDetailPaths(ContentRepositoryConnection connection, List<Page> detailPages, Locale locale, Map<String, Map<Locale, Instant>> detailPaths) {
		ContentApi api;
		try {
			api = contentRepositoryFactory.forConnection(connection).getApi();
		} catch (RuntimeException e) {
			log.warn("Skipping detail pages in sitemap for locale {}: content repository unavailable", locale, e);
			return false;
		}

		boolean complete = true;

		for (Page page : detailPages) {
			String paramField = page.getSitemap().paramField();

			try {
				for (ContentItem item : page.getSitemap().query(api).findAll()) {
					String value = item.getText(paramField);

					if (StringUtils.isNotBlank(value)) {
						String detailPath = page.getFriendlyUrl() + "/" + UriUtils.encodePathSegment(value, StandardCharsets.UTF_8);
						detailPaths.computeIfAbsent(detailPath, path -> new LinkedHashMap<>()).putIfAbsent(locale, getLastModified(item));
					}
				}
			} catch (RuntimeException e) {
				log.warn("Skipping detail pages of page '{}' in sitemap for locale {}", page.getFriendlyUrl(), locale, e);
				complete = false;
			}
		}

		return complete;
	}

	/**
	 * Returns the item's last modified date, or null if the content repository has none or it cannot be read: a
	 * missing lastmod should not drop the URL from the sitemap.
	 */
	private Instant getLastModified(ContentItem item) {
		try {
			// The pattern only applies when formatting the date as a string
			return item.getModified(null).toInstant();
		} catch (RuntimeException e) {
			log.debug("Omitting lastmod of content item {} in sitemap", item.getId(), e);
			return null;
		}
	}

	/**
	 * Builds the URL of the given path in the main locale, with an hreflang alternate for every locale in which the
	 * page is available, and the last modified date if known.
	 */
	private WebPage buildWebPage(String path, Locale mainLocale, List<Locale> availableLocales, Instant lastModified) {
        WebPage.WebPageBuilder webPageBuilder = WebPage.builder().name(mainLocale.getLanguage() + "/" + path);
		availableLocales.forEach(locale -> webPageBuilder.alternateName(locale.getLanguage(), locale.getLanguage() + "/" + path));
		webPageBuilder.alternateName("x-default", "/" + path);
		addLastModified(webPageBuilder, lastModified);
		return webPageBuilder.build();
	}

	private void addLastModified(WebPage.WebPageBuilder webPageBuilder, Instant lastModified) {
		if (lastModified != null) {
			webPageBuilder.lastMod(Date.from(lastModified));
		}
	}

	public String getSitemapUrl(HttpServletRequest request) throws MalformedURLException {
		return getBaseUrl(request) + SeoController.SITEMAP_PATH;
	}

	private String getBaseUrl(HttpServletRequest request) throws MalformedURLException {
		URL url = new URL(request.getRequestURL().toString());

		String protocol = url.getProtocol();
		String host = url.getHost();
		int port = url.getPort();

		if (port == -1) {
			return String.format("%s://%s", protocol, host);
		} else {
			return String.format("%s://%s:%d", protocol, host, port);
		}
	}
}
