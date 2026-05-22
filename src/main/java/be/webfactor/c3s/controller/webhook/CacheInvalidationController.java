package be.webfactor.c3s.controller.webhook;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import be.webfactor.c3s.siteassetstore.cache.SiteAssetStoreCache;
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

@Slf4j
@RestController
@RequiredArgsConstructor
public class CacheInvalidationController {

	private static final String BEARER_PREFIX = "Bearer ";

	private final SiteConnectionRegistryFactory siteConnectionRegistryFactory;
	private final SiteAssetStoreCache siteAssetStoreCache;

	@Value("${c3s.siteassetstore.cache.webhook.secret:}")
	private String siteAssetStoreCacheSecret;

	@PostMapping("/cache/siteassetstore/invalidate")
	public ResponseEntity<Void> invalidateSiteAssetStoreCache(
			@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
			HttpServletRequest request) {
		if (!isAuthorized(authorization)) {
			return ResponseEntity.status(401).build();
		}

		String virtualHost = request.getServerName();
		String repositoryId = resolveRepositoryId(virtualHost);
		if (repositoryId == null) {
			return ResponseEntity.notFound().build();
		}

		siteAssetStoreCache.invalidate(repositoryId);
		log.info("Invalidated asset store cache for virtualHost={} repositoryId={}", virtualHost, repositoryId);
		return ResponseEntity.noContent().build();
	}

	private boolean isAuthorized(String authorization) {
		if (StringUtils.isBlank(siteAssetStoreCacheSecret) || authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
			return false;
		}
		String token = authorization.substring(BEARER_PREFIX.length());
		return MessageDigest.isEqual(token.getBytes(StandardCharsets.UTF_8), siteAssetStoreCacheSecret.getBytes(StandardCharsets.UTF_8));
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
