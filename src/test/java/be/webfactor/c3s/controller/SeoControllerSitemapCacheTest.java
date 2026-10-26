package be.webfactor.c3s.controller;

import be.webfactor.c3s.controller.helper.site.SiteAssetStoreResolver;
import be.webfactor.c3s.controller.sitemap.Sitemap;
import be.webfactor.c3s.controller.sitemap.SitemapBuilder;
import be.webfactor.c3s.renderer.cache.CaffeinePageCache;
import be.webfactor.c3s.siteassetstore.SiteAssetStore;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Verifies that the sitemap is served from the page cache per host, purged together with the site's pages, and not
 * cached when it is incomplete.
 */
class SeoControllerSitemapCacheTest {

	private final SiteAssetStore store = mock(SiteAssetStore.class);
	private final SitemapBuilder sitemapBuilder = mock(SitemapBuilder.class);

	private CaffeinePageCache pageCache;
	private SeoController controller;

	@BeforeEach
	void setUp() throws Exception {
		when(store.getBaseUrl()).thenReturn("site");
		when(sitemapBuilder.getSitemapUrl(any())).thenAnswer(invocation ->
				"https://" + invocation.<HttpServletRequest>getArgument(0).getServerName() + "/sitemap.xml");

		SiteAssetStoreResolver resolver = mock(SiteAssetStoreResolver.class);
		when(resolver.forRequest(any(HttpServletRequest.class))).thenReturn(store);

		pageCache = new CaffeinePageCache(new SimpleMeterRegistry());
		ReflectionTestUtils.setField(pageCache, "enabled", true);
		ReflectionTestUtils.setField(pageCache, "maxEntries", 1000L);
		ReflectionTestUtils.invokeMethod(pageCache, "init");

		controller = new SeoController(resolver, sitemapBuilder, pageCache);
	}

	@Test
	void servesRepeatedRequestsFromCache() throws Exception {
		when(sitemapBuilder.generate(any(), any())).thenReturn(new Sitemap("xml1", true), new Sitemap("xml2", true));

		assertEquals("xml1", sitemap("www.example.com"));
		assertEquals("xml1", sitemap("www.example.com"));
		verify(sitemapBuilder, times(1)).generate(any(), any());
	}

	@Test
	void cachesPerHost() throws Exception {
		when(sitemapBuilder.generate(any(), any())).thenReturn(new Sitemap("www", true), new Sitemap("bare", true));

		assertEquals("www", sitemap("www.example.com"));
		assertEquals("bare", sitemap("example.com"));
		assertEquals("www", sitemap("www.example.com"));
	}

	@Test
	void doesNotCacheIncompleteSitemap() throws Exception {
		when(sitemapBuilder.generate(any(), any())).thenReturn(new Sitemap("partial", false), new Sitemap("full", true));

		assertEquals("partial", sitemap("www.example.com"));
		assertEquals("full", sitemap("www.example.com"));
		assertEquals("full", sitemap("www.example.com"));
	}

	@Test
	void pagePurgeInvalidatesSitemap() throws Exception {
		when(sitemapBuilder.generate(any(), any())).thenReturn(new Sitemap("xml1", true), new Sitemap("xml2", true));

		sitemap("www.example.com");
		pageCache.invalidate("site");

		assertEquals("xml2", sitemap("www.example.com"));
	}

	private String sitemap(String host) throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/sitemap.xml");
		request.setServerName(host);
		return controller.sitemap(request).getBody();
	}
}
