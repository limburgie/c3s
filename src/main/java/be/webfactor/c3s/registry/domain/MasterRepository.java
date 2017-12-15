package be.webfactor.c3s.registry.domain;

import be.webfactor.c3s.repository.RepositoryConnection;

public class MasterRepository {

	private String virtualHost;
	private String name;
	private RepositoryConnection connection;

	public MasterRepository(String virtualHost, String name, RepositoryConnection connection) {
		this.virtualHost = virtualHost;
		this.name = name;
		this.connection = connection;
	}

	public String getVirtualHost() {
		return virtualHost;
	}

	public String getName() {
		return name;
	}

	public RepositoryConnection getConnection() {
		return connection;
	}
}
