package be.webfactor.c3s.siteassetstore;

class SiteAssetStoreNotFoundException extends RuntimeException {

	SiteAssetStoreNotFoundException(SiteAssetStoreConnection connection) {
		super(String.format("No site asset store found for connection %s", connection));
	}
}
