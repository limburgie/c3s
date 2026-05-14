package be.webfactor.c3s.siteassetstore.cache;

import java.util.function.Supplier;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SiteAssetStoreFileCache {

	@Getter
    @Value("${c3s.assetstore.cache.enabled:true}")
	private boolean enabled;

	@Value("${c3s.assetstore.cache.max-entries:10000}")
	private long maxEntries;

	private Cache<CacheKey, byte[]> bytesCache;
	private Cache<CacheKey, String> stringCache;

	@PostConstruct
	void init() {
		bytesCache = Caffeine.newBuilder().maximumSize(maxEntries).build();
		stringCache = Caffeine.newBuilder().maximumSize(maxEntries).build();
	}

    public String getOrLoadString(String basePath, String relativePath, Supplier<String> loader) {
		if (!enabled) {
			return loader.get();
		}
		return stringCache.get(new CacheKey(basePath, relativePath), k -> loader.get());
	}

	public byte[] getOrLoadBytes(String basePath, String relativePath, Supplier<byte[]> loader) {
		if (!enabled) {
			return loader.get();
		}
		return bytesCache.get(new CacheKey(basePath, relativePath), k -> loader.get());
	}

	public void invalidate(String basePath) {
		bytesCache.asMap().keySet().removeIf(k -> k.basePath().equals(basePath));
		stringCache.asMap().keySet().removeIf(k -> k.basePath().equals(basePath));
	}

	public void invalidate(String basePath, String relativePath) {
		CacheKey key = new CacheKey(basePath, relativePath);
		bytesCache.invalidate(key);
		stringCache.invalidate(key);
	}

	public void invalidateAll() {
		bytesCache.invalidateAll();
		stringCache.invalidateAll();
	}

	private record CacheKey(String basePath, String relativePath) {}
}
