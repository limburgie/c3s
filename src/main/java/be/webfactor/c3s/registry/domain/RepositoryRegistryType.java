package be.webfactor.c3s.registry.domain;

public enum RepositoryRegistryType {

	DATABASE, LOCAL, PRISMIC;

	private static final RepositoryRegistryType DEFAULT_TYPE = LOCAL;

	public static RepositoryRegistryType get(String registryType) {
		return registryType == null ? DEFAULT_TYPE : valueOf(registryType.toUpperCase());
	}
}
