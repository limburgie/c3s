package be.webfactor.c3s.siteconnectionregistry.domain;

import be.webfactor.c3s.siteassetstore.SiteAssetStoreConnection;

public record SiteConnection(String virtualHost, String name, SiteAssetStoreConnection connection) {

}
