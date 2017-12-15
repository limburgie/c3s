package be.webfactor.c3s.registry.service;

import be.webfactor.c3s.registry.domain.MasterRepository;
import be.webfactor.c3s.registry.domain.RepositoryRegistryType;

public interface RepositoryRegistry {

	/**
	 * Find a master repository inside the registry, given the virtual host.
	 * @param virtualHost the virtual host to find the registry for.
	 * @return the repository matching the given virtual host.
	 */
	MasterRepository findMasterRepository(String virtualHost);

	/**
	 * Returns the type of this repository.
	 * @return type of the repository.
	 */
	RepositoryRegistryType getType();
}
