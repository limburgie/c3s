package be.webfactor.c3s.registry.domain;

import be.webfactor.c3s.repository.RepositoryConnection;
import lombok.Value;

@Value
public class MasterRepository {

	String virtualHost;
	String name;
	RepositoryConnection connection;
}
