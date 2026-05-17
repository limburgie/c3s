package be.webfactor.c3s.siteconnectionregistry;

public enum SiteConnectionRegistryType {

	DATABASE, LOCAL;

	private static final SiteConnectionRegistryType DEFAULT = LOCAL;

	public static SiteConnectionRegistryType forKey(String registryType) {
		return registryType == null ? DEFAULT : valueOf(registryType.toUpperCase());
	}
}
