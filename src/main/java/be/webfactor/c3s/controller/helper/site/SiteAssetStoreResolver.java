package be.webfactor.c3s.controller.helper.site;

import be.webfactor.c3s.siteassetstore.SiteAssetStore;
import be.webfactor.c3s.siteassetstore.SiteAssetStoreFactory;
import be.webfactor.c3s.siteconnectionregistry.SiteConnectionRegistryFactory;
import be.webfactor.c3s.siteconnectionregistry.domain.SiteConnection;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Resolves the site asset store of the site that is requested, based on the virtual host of the request.
 */
@Service
@RequiredArgsConstructor
public class SiteAssetStoreResolver {

	private final SiteConnectionRegistryFactory siteConnectionRegistryFactory;
	private final SiteAssetStoreFactory siteAssetStoreFactory;

	public SiteAssetStore forRequest(HttpServletRequest request) {
		SiteConnection repository = siteConnectionRegistryFactory.getRegistry().getConnectionForHost(request.getServerName());

		return siteAssetStoreFactory.forConnection(repository.connection());
	}
}
