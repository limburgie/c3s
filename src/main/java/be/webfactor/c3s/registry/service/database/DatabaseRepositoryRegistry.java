package be.webfactor.c3s.registry.service.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.registry.domain.MasterRepository;
import be.webfactor.c3s.registry.domain.RepositoryRegistryType;
import be.webfactor.c3s.registry.service.RepositoryRegistry;
import be.webfactor.c3s.registry.service.database.domain.PersistedRepository;
import be.webfactor.c3s.registry.service.database.repository.PersistedRepositoryRepository;
import be.webfactor.c3s.repository.RepositoryConnection;

@Service
public class DatabaseRepositoryRegistry implements RepositoryRegistry {

	@Autowired private PersistedRepositoryRepository repository;

	public MasterRepository findMasterRepository(String virtualHost) {
		PersistedRepository repo = repository.findByVirtualHost(virtualHost);
		RepositoryConnection connection = new RepositoryConnection(repo.getType(), repo.getConnectionId(), repo.getAccessToken());

		return new MasterRepository(virtualHost, repo.getName(), connection);
	}

	public RepositoryRegistryType getType() {
		return RepositoryRegistryType.DATABASE;
	}
}
