package be.webfactor.c3s.siteconnectionregistry.database;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.siteassetstore.SiteAssetStoreConnection;
import be.webfactor.c3s.siteconnectionregistry.domain.SiteConnection;
import be.webfactor.c3s.siteconnectionregistry.SiteConnectionRegistryType;
import be.webfactor.c3s.siteconnectionregistry.SiteConnectionRegistry;

@Service
@RequiredArgsConstructor
public class DatabaseSiteConnectionRegistry implements SiteConnectionRegistry {

	private final DatabaseSiteConnectionRepository repository;

	public SiteConnection getConnectionForHost(String virtualHost) {
		DatabaseSiteConnection repo = repository.findByVirtualHost(virtualHost);
		SiteAssetStoreConnection connection = new SiteAssetStoreConnection(repo.getType(), repo.getUrl(), repo.getPrincipal(), repo.getSecret());

		return new SiteConnection(virtualHost, repo.getName(), connection);
	}

	public SiteConnectionRegistryType getType() {
		return SiteConnectionRegistryType.DATABASE;
	}
}
