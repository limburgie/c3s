package be.webfactor.c3s.siteconnectionregistry.local;

import be.webfactor.c3s.siteassetstore.SiteAssetStoreConnection;
import be.webfactor.c3s.siteassetstore.SiteAssetStoreType;
import be.webfactor.c3s.siteconnectionregistry.domain.SiteConnection;
import be.webfactor.c3s.siteconnectionregistry.SiteConnectionRegistry;
import be.webfactor.c3s.siteconnectionregistry.SiteConnectionRegistryType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocalSiteConnectionRegistry implements SiteConnectionRegistry {

	private static final String SITE_CONNECTION_PROP_FORMAT = "c3s.siteconnectionregistry.%s.%s";

	private final Environment env;

	public SiteConnection getConnectionForHost(String virtualHost) {
		return new SiteConnection(
				virtualHost,
				getPropValue(virtualHost, "name"),
				new SiteAssetStoreConnection(
						SiteAssetStoreType.valueOf(getPropValue(virtualHost, "type")),
						getPropValue(virtualHost, "url"),
						getPropValue(virtualHost, "principal"),
						getPropValue(virtualHost, "secret"),
						getPropValue(virtualHost, "location")
				)
		);
	}

	private String getPropValue(String virtualHost, String propertyName) {
		return env.getProperty(String.format(SITE_CONNECTION_PROP_FORMAT, virtualHost, propertyName));
	}

	public SiteConnectionRegistryType getType() {
		return SiteConnectionRegistryType.LOCAL;
	}
}
