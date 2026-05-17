package be.webfactor.c3s.siteassetstore.cache;

import java.util.function.Supplier;

public interface SiteAssetStoreCache {

    String getOrLoadString(String basePath, String relativePath, Supplier<String> loader);

    byte[] getOrLoadBytes(String basePath, String relativePath, Supplier<byte[]> loader);

    void invalidate(String basePath);
}
