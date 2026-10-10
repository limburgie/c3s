package be.webfactor.c3s.controller.sitemap;

import be.webfactor.c3s.controller.SeoController;
import be.webfactor.c3s.siteassetstore.domain.Page;
import be.webfactor.c3s.siteassetstore.SiteAssetStore;
import cz.jiripinkas.jsitemapgenerator.WebPage;
import cz.jiripinkas.jsitemapgenerator.generator.SitemapGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class SitemapBuilder {

	public String generate(HttpServletRequest request, SiteAssetStore siteAssetStore) throws MalformedURLException {
        return SitemapGenerator.of(getBaseUrl(request)).addPages(createWebPages(siteAssetStore)).toString();
	}

	private List<WebPage> createWebPages(SiteAssetStore siteAssetStore) {
		List<Locale> locales = siteAssetStore.getLocales();
		List<Page> pages = getNonIndexPages(siteAssetStore);

		List<WebPage> webPages = new ArrayList<>();

		if (locales.isEmpty()) {
			webPages.add(WebPage.builder().nameRoot().build());
			for (Page page : pages) {
				webPages.add(WebPage.builder().name(page.getFriendlyUrl()).build());
			}
		} else {
			for (Locale locale : locales) {
				webPages.add(buildWebPage("", locale, locales));
				for (Page page : pages) {
					webPages.add(buildWebPage(page.getFriendlyUrl(), locale, locales));
				}
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

	private WebPage buildWebPage(String friendlyUrl, Locale mainLocale, List<Locale> allLocales) {
        WebPage.WebPageBuilder webPageBuilder = WebPage.builder().name(mainLocale.getLanguage() + "/" + friendlyUrl);
		allLocales.forEach(locale -> webPageBuilder.alternateName(locale.getLanguage(), locale.getLanguage() + "/" + friendlyUrl));
		webPageBuilder.alternateName("x-default", "/" + friendlyUrl);
		return webPageBuilder.build();
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
