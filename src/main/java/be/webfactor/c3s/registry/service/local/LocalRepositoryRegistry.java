package be.webfactor.c3s.registry.service.local;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.registry.service.RepositoryRegistry;
import be.webfactor.c3s.registry.domain.MasterRepository;
import be.webfactor.c3s.registry.domain.RepositoryRegistryType;
import be.webfactor.c3s.repository.RepositoryConnection;
import be.webfactor.c3s.repository.RepositoryType;

@Service
public class LocalRepositoryRegistry implements RepositoryRegistry {

	private static final String REPOSITORY_NAME_PROP_FORMAT = "c3s.local.registry.%s.name";
	private static final String REPOSITORY_TYPE_PROP_FORMAT = "c3s.local.registry.%s.type";
	private static final String REPOSITORY_ID_PROP_FORMAT = "c3s.local.registry.%s.id";
	private static final String REPOSITORY_ACCESS_TOKEN_PROP_FORMAT = "c3s.local.registry.%s.access.token";

	@Autowired private Environment env;

	public MasterRepository findMasterRepository(String virtualHost) {
		String name = getPropValue(REPOSITORY_NAME_PROP_FORMAT, virtualHost);
		RepositoryType type = RepositoryType.valueOf(getPropValue(REPOSITORY_TYPE_PROP_FORMAT, virtualHost));
		String id = getPropValue(REPOSITORY_ID_PROP_FORMAT, virtualHost);
		String accessToken = getPropValue(REPOSITORY_ACCESS_TOKEN_PROP_FORMAT, virtualHost);

		return new MasterRepository(virtualHost, name, new RepositoryConnection(type, id, accessToken));
	}

	private String getPropValue(String format, String virtualHost) {
		return env.getProperty(String.format(format, virtualHost));
	}

	public RepositoryRegistryType getType() {
		return RepositoryRegistryType.LOCAL;
	}
}
