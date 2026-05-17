package be.webfactor.c3s.siteconnectionregistry;

import be.webfactor.c3s.siteconnectionregistry.domain.SiteConnection;

/**
 * The repository registry holds the location and credentials of all master repositories registered in this system.
 * It basically maps a virtual host to a master repository, which can in turn be used to initialize a master service.
 */
public interface SiteConnectionRegistry {

	/**
	 * Find a master repository inside the registry, given the virtual host.
	 */
	SiteConnection getConnectionForHost(String virtualHost);

	/**
	 * Returns the type of this registry.
	 */
	SiteConnectionRegistryType getType();
}
