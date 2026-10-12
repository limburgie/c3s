package be.webfactor.c3s.controller.webhook;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.function.Consumer;

import be.webfactor.c3s.renderer.cache.PageCache;
import be.webfactor.c3s.siteassetstore.cache.AssetCache;
import be.webfactor.c3s.siteconnectionregistry.SiteConnectionRegistryFactory;
import be.webfactor.c3s.siteconnectionregistry.domain.SiteConnection;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Webhooks that purge the caches of the site identified by the request's virtual host. Each cache has its own bearer
 * secret. Purging the asset cache also purges the page cache, since rendered pages are derived from the assets.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CachePurgeController {

	private static final String BEARER_PREFIX = "Bearer ";

	private final SiteConnectionRegistryFactory siteConnectionRegistryFactory;
	private final AssetCache assetCache;
	private final PageCache pageCache;

	@Value("${c3s.assets.cache.webhook.secret:}")
	private String assetsCacheSecret;

	@Value("${c3s.pages.cache.webhook.secret:}")
	private String pagesCacheSecret;

	@PostMapping("/cache/assets/purge")
	public ResponseEntity<Void> purgeAssetsCache(
			@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
			HttpServletRequest request) {
		return purge(authorization, assetsCacheSecret, request, "asset and page", repositoryId -> {
			assetCache.invalidate(repositoryId);
			pageCache.invalidate(repositoryId);
		});
	}

	@PostMapping("/cache/pages/purge")
	public ResponseEntity<Void> purgePagesCache(
			@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
			HttpServletRequest request) {
		return purge(authorization, pagesCacheSecret, request, "page", pageCache::invalidate);
	}

	private ResponseEntity<Void> purge(String authorization, String secret, HttpServletRequest request, String cacheName, Consumer<String> invalidator) {
		if (!isAuthorized(authorization, secret)) {
			return ResponseEntity.status(401).build();
		}

		String virtualHost = request.getServerName();
		String repositoryId = resolveRepositoryId(virtualHost);
		if (repositoryId == null) {
			return ResponseEntity.notFound().build();
		}

		invalidator.accept(repositoryId);
		log.info("Purged {} cache for virtualHost={} repositoryId={}", cacheName, virtualHost, repositoryId);
		return ResponseEntity.noContent().build();
	}

	private boolean isAuthorized(String authorization, String secret) {
		if (StringUtils.isBlank(secret) || authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
			return false;
		}
		String token = authorization.substring(BEARER_PREFIX.length());
		return MessageDigest.isEqual(token.getBytes(StandardCharsets.UTF_8), secret.getBytes(StandardCharsets.UTF_8));
	}

	private String resolveRepositoryId(String virtualHost) {
		try {
			SiteConnection siteConnection = siteConnectionRegistryFactory.getRegistry().getConnectionForHost(virtualHost);
			if (siteConnection == null || siteConnection.connection() == null) {
				return null;
			}
			return StringUtils.trimToNull(siteConnection.connection().getRepositoryId());
		} catch (Exception e) {
			return null;
		}
	}
}
