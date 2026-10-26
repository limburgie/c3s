package be.webfactor.c3s.renderer.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.Ticker;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class CaffeinePageCache implements PageCache {

	private final MeterRegistry meterRegistry;

	@Value("${c3s.pages.cache.enabled:true}")
	private boolean enabled;

	@Value("${c3s.pages.cache.max-entries:1000}")
	private long maxEntries;

	private Cache<PageCacheKey, CachedPage> cache;

	@PostConstruct
	void init() {
		init(Ticker.systemTicker());
	}

	void init(Ticker ticker) {
		cache = Caffeine.newBuilder()
				.maximumSize(maxEntries)
				.expireAfter(Expiry.writing((PageCacheKey _, CachedPage page) -> page.ttl()))
				.ticker(ticker)
				.recordStats()
				.build();
		CaffeineCacheMetrics.monitor(meterRegistry, cache, "pages");
	}

	@Override
	public String getOrRenderConditionally(PageCacheKey key, Long ttlSeconds, Supplier<Rendered> renderer) {
		if (!enabled || (ttlSeconds != null && ttlSeconds <= 0)) {
			return renderer.get().html();
		}

		// No cache.get(key, loader): rendering is slow and would block other keys in the same hash bin.
		// Two concurrent misses on the same page may both render, which is harmless.
		CachedPage cached = cache.getIfPresent(key);
		if (cached != null) {
			return cached.html();
		}

		Rendered rendered = renderer.get();
		if (rendered.cacheable()) {
			cache.put(key, new CachedPage(rendered.html(), ttlSeconds == null ? ChronoUnit.FOREVER.getDuration() : Duration.ofSeconds(ttlSeconds)));
		}
		return rendered.html();
	}

	@Override
	public void invalidate(String site) {
		cache.asMap().keySet().removeIf(k -> k.site().equals(site));
	}

	private record CachedPage(String html, Duration ttl) {}
}
