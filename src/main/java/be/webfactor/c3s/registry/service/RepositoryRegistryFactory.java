package be.webfactor.c3s.registry.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.registry.domain.RepositoryRegistryType;

@Service
public class RepositoryRegistryFactory {

	@Autowired private Environment env;
	@Autowired private List<RepositoryRegistry> registries;

	private RepositoryRegistry registry;

	@PostConstruct
	public void init() {
		String registryType = env.getProperty("c3s.registry.type");
		RepositoryRegistryType type = RepositoryRegistryType.get(registryType);

		for (RepositoryRegistry registry : registries) {
			if (registry.getType() == type) {
				this.registry = registry;
			}
		}
	}

	public RepositoryRegistry getRegistry() {
		return registry;
	}
}
