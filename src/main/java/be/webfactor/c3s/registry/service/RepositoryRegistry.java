package be.webfactor.c3s.registry.service;

import be.webfactor.c3s.registry.domain.MasterRepository;
import be.webfactor.c3s.registry.domain.RepositoryRegistryType;

/**
 * The repository registry holds the location and credentials of all master repositories registered in this system.
 * It basically maps a virtual host to a master repository, which can in turn be used to initialize a master service.
 */
public interface RepositoryRegistry {

	/**
	 * Find a master repository inside the registry, given the virtual host.
	 */
	MasterRepository findMasterRepository(String virtualHost);

	/**
	 * Returns the type of this registry.
	 */
	RepositoryRegistryType getType();
}
