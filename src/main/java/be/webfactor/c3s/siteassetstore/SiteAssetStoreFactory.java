package be.webfactor.c3s.siteassetstore;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SiteAssetStoreFactory {

	private final List<SiteAssetStore> siteAssetStores;

	public SiteAssetStore forConnection(SiteAssetStoreConnection connection) {
		SiteAssetStore siteAssetStore = getSiteAssetStore(connection);
		siteAssetStore.initialize(connection);

		return siteAssetStore;
	}

	private SiteAssetStore getSiteAssetStore(SiteAssetStoreConnection connection) {
		return siteAssetStores.stream()
				.filter(ms -> ms.getType() == connection.getType())
				.findFirst()
				.orElseThrow(() -> new SiteAssetStoreNotFoundException(connection));
	}
}
