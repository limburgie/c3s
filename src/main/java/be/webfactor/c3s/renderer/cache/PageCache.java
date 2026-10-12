package be.webfactor.c3s.renderer.cache;

import java.util.function.Supplier;

/**
 * Caches fully rendered pages per site, until the site's page cache is purged.
 */
public interface PageCache {

	/**
	 * Returns the cached HTML for the given key, rendering and caching it via {@code renderer} on a miss.
	 * A {@code null} TTL caches the page indefinitely, a TTL of 0 never caches it.
	 * Exceptions thrown by the renderer propagate and nothing is cached.
	 */
	String getOrRender(PageCacheKey key, Long ttlSeconds, Supplier<String> renderer);

	/**
	 * Removes all cached pages of the given site.
	 */
	void invalidate(String site);
}
