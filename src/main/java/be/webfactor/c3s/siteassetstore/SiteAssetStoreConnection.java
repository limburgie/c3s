package be.webfactor.c3s.siteassetstore;

import lombok.Value;

@Value
public class SiteAssetStoreConnection {

	SiteAssetStoreType type;
	String repositoryId;
	String accessToken;
	String secretKey;

	public static SiteAssetStoreConnection of(SiteAssetStoreType type, String repositoryId) {
		return new SiteAssetStoreConnection(type, repositoryId, null, null);
	}
}
