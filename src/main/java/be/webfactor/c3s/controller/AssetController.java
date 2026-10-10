package be.webfactor.c3s.controller;

import be.webfactor.c3s.controller.helper.asset.Asset;
import be.webfactor.c3s.controller.helper.asset.AssetService;
import be.webfactor.c3s.controller.helper.site.SiteAssetStoreResolver;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Serves the static assets (stylesheets, scripts, images, ...) of the requested site from its site asset store.
 */
@RestController
@RequiredArgsConstructor
public class AssetController {

	public static final String ASSETS_PREFIX = "/assets/";

	private final SiteAssetStoreResolver siteAssetStoreResolver;
	private final AssetService assetService;

	@GetMapping(ASSETS_PREFIX + "**")
	public ResponseEntity<byte[]> asset(HttpServletRequest request) throws IOException {
		Asset asset = assetService.getAsset(request, siteAssetStoreResolver.forRequest(request));

		return ResponseEntity.ok()
				.cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
				.contentType(asset.contentType())
				.body(asset.data());
	}
}
