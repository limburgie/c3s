package be.webfactor.c3s.renderer.cache;

import be.webfactor.c3s.siteassetstore.domain.LocaleContext;
import com.github.benmanes.caffeine.cache.Ticker;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies that rendered pages are cached per key until their TTL expires or their site is purged, that a TTL of 0
 * disables caching and that failed or uncacheable renders are never cached.
 */
class CaffeinePageCacheTest {

	private static final LocaleContext NL = new LocaleContext(Locale.of("nl"), false, true);
	private static final LocaleContext EN = new LocaleContext(Locale.of("en"), false, true);

	private final AtomicLong nanos = new AtomicLong();
	private final AtomicInteger renders = new AtomicInteger();

	private CaffeinePageCache cache;

	@BeforeEach
	void setUp() {
		cache = cache(true, 1000);
	}

	@Test
	void cachesIndefinitelyWithoutTtl() {
		PageCacheKey key = key("site", NL, "home");

		assertEquals("html1", render(key, null));
		nanos.addAndGet(TimeUnit.DAYS.toNanos(3650));

		assertEquals("html1", render(key, null));
		assertEquals(1, renders.get());
	}

	@Test
	void cachesPerSiteLocaleAndPath() {
		render(key("site", NL, "home"), null);
		render(key("site", EN, "home"), null);
		render(key("site", NL, "news/article-1"), null);
		render(key("site", NL, "news/article-2"), null);
		render(key("other", NL, "home"), null);

		assertEquals(5, renders.get());
	}

	@Test
	void expiresAfterTtl() {
		PageCacheKey key = key("site", NL, "home");

		render(key, 60L);
		nanos.addAndGet(TimeUnit.SECONDS.toNanos(59));
		render(key, 60L);
		assertEquals(1, renders.get());

		nanos.addAndGet(TimeUnit.SECONDS.toNanos(2));
		assertEquals("html2", render(key, 60L));
	}

	@Test
	void neverCachesWithZeroTtl() {
		PageCacheKey key = key("site", NL, "home");

		render(key, 0L);
		render(key, 0L);

		assertEquals(2, renders.get());
	}

	@Test
	void invalidateOnlyPurgesGivenSite() {
		PageCacheKey site = key("site", NL, "home");
		PageCacheKey other = key("other", NL, "home");
		render(site, null);
		render(other, null);

		cache.invalidate("site");
		render(site, null);
		render(other, null);

		assertEquals(3, renders.get());
	}

	@Test
	void doesNotCacheFailedRenders() {
		PageCacheKey key = key("site", NL, "home");

		assertThrows(IllegalStateException.class, () -> cache.getOrRender(key, null, () -> { throw new IllegalStateException(); }));

		assertEquals("html1", render(key, null));
	}

	@Test
	void doesNotCacheUncacheableRenders() {
		PageCacheKey key = key("site", NL, "sitemap");

		assertEquals("partial", cache.getOrRenderConditionally(key, null, () -> new PageCache.Rendered("partial", false)));

		assertEquals("html1", render(key, null));
		assertEquals("html1", render(key, null));
	}

	@Test
	void rendersEveryTimeWhenDisabled() {
		cache = cache(false, 1000);
		PageCacheKey key = key("site", NL, "home");

		render(key, null);
		render(key, null);

		assertEquals(2, renders.get());
	}

	private String render(PageCacheKey key, Long ttl) {
		return cache.getOrRender(key, ttl, () -> "html" + renders.incrementAndGet());
	}

	private CaffeinePageCache cache(boolean enabled, long maxEntries) {
		CaffeinePageCache result = new CaffeinePageCache(new SimpleMeterRegistry());
		ReflectionTestUtils.setField(result, "enabled", enabled);
		ReflectionTestUtils.setField(result, "maxEntries", maxEntries);
		result.init((Ticker) nanos::get);
		return result;
	}

	private static PageCacheKey key(String site, LocaleContext localeContext, String path) {
		return new PageCacheKey(site, localeContext, path);
	}
}
