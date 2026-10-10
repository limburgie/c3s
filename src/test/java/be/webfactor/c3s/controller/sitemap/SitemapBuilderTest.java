package be.webfactor.c3s.controller.sitemap;

import be.webfactor.c3s.siteassetstore.SiteAssetStore;
import be.webfactor.c3s.siteassetstore.domain.Page;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Verifies that the sitemap lists every visible page, including nested child pages, and lists the index page only
 * once (at the root URL), even when the index page is not the first page in the site configuration.
 */
class SitemapBuilderTest {

	private final SitemapBuilder sitemapBuilder = new SitemapBuilder();

	@Test
	void includesNestedChildPages() throws Exception {
		Page about = page("about", page("team", page("history")));
		SiteAssetStore store = store(List.of(page("home"), about), page("home"), List.of());

		Set<String> locs = locs(sitemapBuilder.generate(request(), store));

		assertEquals(Set.of("https://www.example.com/", "https://www.example.com/about",
				"https://www.example.com/team", "https://www.example.com/history"), locs);
	}

	@Test
	void excludesConfiguredIndexPageInsteadOfFirstPage() throws Exception {
		SiteAssetStore store = store(List.of(page("intro"), page("home"), page("contact")), page("home"), List.of());

		Set<String> locs = locs(sitemapBuilder.generate(request(), store));

		assertEquals(Set.of("https://www.example.com/", "https://www.example.com/intro",
				"https://www.example.com/contact"), locs);
	}

	@Test
	void prefixesEveryPageWithEachLocale() throws Exception {
		SiteAssetStore store = store(List.of(page("home"), page("contact", page("route"))), page("home"),
				List.of(Locale.of("nl"), Locale.of("fr")));

		Set<String> locs = locs(sitemapBuilder.generate(request(), store));

		assertEquals(Set.of("https://www.example.com/fr/", "https://www.example.com/fr/contact",
				"https://www.example.com/fr/route", "https://www.example.com/nl/", "https://www.example.com/nl/contact",
				"https://www.example.com/nl/route"), locs);
	}

	@Test
	void sitemapUrlUsesRequestHost() throws Exception {
		assertEquals("https://www.example.com/sitemap.xml", sitemapBuilder.getSitemapUrl(request()));
	}

	private static Page page(String friendlyUrl, Page... children) {
		return Page.builder().friendlyUrl(friendlyUrl).name(friendlyUrl).children(List.of(children)).build();
	}

	private static SiteAssetStore store(List<Page> pages, Page indexPage, List<Locale> locales) {
		SiteAssetStore store = mock(SiteAssetStore.class);
		when(store.getPages(false)).thenReturn(pages);
		when(store.getIndexPage()).thenReturn(indexPage);
		when(store.getLocales()).thenReturn(locales);
		return store;
	}

	private static MockHttpServletRequest request() {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/sitemap.xml");
		request.setScheme("https");
		request.setServerName("www.example.com");
		request.setServerPort(443);
		return request;
	}

	/**
	 * The generator sorts URLs alphabetically, so only the set of URLs is meaningful. Duplicates fail the test.
	 */
	private static Set<String> locs(String sitemapXml) {
		Matcher matcher = Pattern.compile("<loc>([^<]*)</loc>").matcher(sitemapXml);
		List<String> locs = matcher.results().map(result -> result.group(1)).toList();
		assertEquals(locs.size(), Set.copyOf(locs).size(), "Duplicate URLs in sitemap: " + locs);
		return Set.copyOf(locs);
	}
}
