package be.webfactor.c3s.controller;

import be.webfactor.c3s.controller.helper.site.SiteAssetStoreResolver;
import be.webfactor.c3s.controller.sitemap.Sitemap;
import be.webfactor.c3s.controller.sitemap.SitemapBuilder;
import be.webfactor.c3s.renderer.cache.PageCache;
import be.webfactor.c3s.renderer.cache.PageCacheKey;
import be.webfactor.c3s.siteassetstore.SiteAssetNotFoundException;
import be.webfactor.c3s.siteassetstore.SiteAssetStore;
import be.webfactor.c3s.siteassetstore.domain.LocaleContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Serves the well-known root files that browsers and search engines request directly: favicons, the sitemap and robots.txt.
 */
@RestController
@RequiredArgsConstructor
public class SeoController {

	public static final String SITEMAP_PATH = "/sitemap.xml";
	private static final String ROBOTS_TXT_FILENAME = "robots.txt";
	private static final String FAVICON_FOLDER = AssetController.ASSETS_PREFIX + "img/favicon";
	private static final String FAVICON_ICO_FILENAME = "favicon.ico";
	private static final String FAVICON_SVG_FILENAME = "favicon.svg";
	/**
	 * Purges cover content edits and site deploys, the TTL covers content that becomes visible over time, such as
	 * items with a date filter.
	 */
	private static final long SITEMAP_CACHE_TTL_SECONDS = TimeUnit.DAYS.toSeconds(1);

	private final SiteAssetStoreResolver siteAssetStoreResolver;
	private final SitemapBuilder sitemapBuilder;
	private final PageCache pageCache;

	@GetMapping("/" + FAVICON_ICO_FILENAME)
	public ResponseEntity<byte[]> faviconIco(HttpServletRequest request) {
		return favicon(request, FAVICON_ICO_FILENAME, "image/x-icon");
	}

	@GetMapping("/" + FAVICON_SVG_FILENAME)
	public ResponseEntity<byte[]> faviconSvg(HttpServletRequest request) {
		return favicon(request, FAVICON_SVG_FILENAME, "image/svg+xml");
	}

	private ResponseEntity<byte[]> favicon(HttpServletRequest request, String fileName, String mediaType) {
		byte[] data = siteAssetStoreResolver.forRequest(request).readAsset(FAVICON_FOLDER + "/" + fileName);
		return ResponseEntity.ok()
				.cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
				.contentType(MediaType.parseMediaType(mediaType))
				.body(data);
	}

	/**
	 * Serves the sitemap from the page cache, so it is purged together with the site's pages. An incomplete sitemap
	 * (content repository failure) is served but not cached.
	 */
	@GetMapping(value = SITEMAP_PATH, produces = MediaType.TEXT_XML_VALUE)
	public ResponseEntity<String> sitemap(HttpServletRequest request) throws MalformedURLException {
		SiteAssetStore siteAssetStore = siteAssetStoreResolver.forRequest(request);
		PageCacheKey cacheKey = new PageCacheKey(siteAssetStore.getBaseUrl(), new LocaleContext(), sitemapBuilder.getSitemapUrl(request));

		String sitemapXml = pageCache.getOrRenderConditionally(cacheKey, SITEMAP_CACHE_TTL_SECONDS, () -> {
			try {
				Sitemap sitemap = sitemapBuilder.generate(request, siteAssetStore);
				return new PageCache.Rendered(sitemap.xml(), sitemap.complete());
			} catch (MalformedURLException e) {
				throw new UncheckedIOException(e);
			}
		});

		return ResponseEntity.ok()
				.cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
				.contentType(MediaType.TEXT_XML)
				.body(sitemapXml);
	}

	/**
	 * Serves the site's own robots.txt from the root of its asset store if it has one, otherwise a default that
	 * allows all crawling and points crawlers to the sitemap.
	 */
	@GetMapping(value = "/" + ROBOTS_TXT_FILENAME, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> robotsTxt(HttpServletRequest request) throws MalformedURLException {
		SiteAssetStore siteAssetStore = siteAssetStoreResolver.forRequest(request);

		String robotsTxt;
		try {
			robotsTxt = siteAssetStore.readResource(ROBOTS_TXT_FILENAME);
		} catch (SiteAssetNotFoundException e) {
			robotsTxt = "User-agent: *\nAllow: /\n\nSitemap: " + sitemapBuilder.getSitemapUrl(request) + "\n";
		}

		return ResponseEntity.ok()
				.cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
				.contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
				.body(robotsTxt);
	}
}
