package be.webfactor.c3s.registry.service.prismic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.registry.service.RepositoryRegistry;
import be.webfactor.c3s.registry.domain.MasterRepository;
import be.webfactor.c3s.registry.domain.RepositoryRegistryType;
import be.webfactor.c3s.repository.RepositoryConnection;
import be.webfactor.c3s.repository.RepositoryType;
import io.prismic.Api;
import io.prismic.Document;

@Service
public class PrismicRepositoryRegistry implements RepositoryRegistry {

	@Value("${c3s.prismic.registry.endpoint}:#{null}")
	private String prismicRegistryEndpoint;

	@Value("${c3s.prismic.registry.access.token}:#{null}")
	private String prismicRegistryAccessToken;

	public MasterRepository findMasterRepository(String virtualHost) {
		Document repositoryDocument = Api.get(prismicRegistryEndpoint, prismicRegistryAccessToken).getByUID("repository", virtualHost);

		String name = repositoryDocument.getText("repository.name");
		RepositoryType type = RepositoryType.valueOf(repositoryDocument.getText("repository.type"));
		String id = repositoryDocument.getText("repository.id");
		String accessToken = repositoryDocument.getText("repository.access_token");

		return new MasterRepository(virtualHost, name, new RepositoryConnection(type, id, accessToken));
	}

	public RepositoryRegistryType getType() {
		return RepositoryRegistryType.PRISMIC;
	}
}
