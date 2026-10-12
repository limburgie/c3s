package be.webfactor.c3s.siteassetstore.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class CaffeineAssetCache implements AssetCache {

	private final MeterRegistry meterRegistry;

	@Value("${c3s.assets.cache.enabled:true}")
	private boolean enabled;

	@Value("${c3s.assets.cache.max-entries:5000}")
	private long maxEntries;

	private Cache<CacheKey, byte[]> bytesCache;
	private Cache<CacheKey, String> stringCache;

	@PostConstruct
	void init() {
		bytesCache = Caffeine.newBuilder().maximumSize(maxEntries).recordStats().build();
		stringCache = Caffeine.newBuilder().maximumSize(maxEntries).recordStats().build();
		CaffeineCacheMetrics.monitor(meterRegistry, bytesCache, "assets.bytes");
		CaffeineCacheMetrics.monitor(meterRegistry, stringCache, "assets.strings");
	}

	@Override
    public String getOrLoadString(String basePath, String relativePath, Supplier<String> loader) {
		if (!enabled) {
			return loader.get();
		}
		return stringCache.get(new CacheKey(basePath, relativePath), _ -> loader.get());
	}

	@Override
	public byte[] getOrLoadBytes(String basePath, String relativePath, Supplier<byte[]> loader) {
		if (!enabled) {
			return loader.get();
		}
		return bytesCache.get(new CacheKey(basePath, relativePath), _ -> loader.get());
	}

	@Override
	public void invalidate(String basePath) {
		bytesCache.asMap().keySet().removeIf(k -> k.basePath().equals(basePath));
		stringCache.asMap().keySet().removeIf(k -> k.basePath().equals(basePath));
	}

	private record CacheKey(String basePath, String relativePath) {}
}
