package be.webfactor.c3s.contentrepository.paginea;

import be.webfactor.c3s.contentrepository.domain.ContentApi;
import be.webfactor.c3s.contentrepository.domain.ContentItem;
import be.webfactor.c3s.contentrepository.domain.QueryBuilder;
import be.webfactor.c3s.contentrepository.paginea.api.ContentItemsApi;
import be.webfactor.c3s.siteassetstore.domain.LocationThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import java.util.Locale;

public class PagineaContentApi implements ContentApi {

    private final String siteKey;
    private final ContentItemsApi api;

    public PagineaContentApi(String repositoryId) {
        String[] parts = StringUtils.splitByWholeSeparator(repositoryId, "/api/");
        this.siteKey = parts[1];
        ApiClient apiClient = new ApiClient();
        apiClient.addDefaultHeader(HttpHeaders.ACCEPT_LANGUAGE, getLanguage());
        apiClient.setBasePath(parts[0]);
        api = new ContentItemsApi(apiClient);
    }

    @Override
    public QueryBuilder query(String type) {
        return new PagineaQueryBuilder(siteKey, type, api);
    }

    @Override
    public ContentItem findById(String id) {
        return new PagineaContentItem(api.getContentItem(siteKey, id));
    }

    @Override
    public Object nativeApi() {
        return api;
    }

    private static String getLanguage() {
        Locale locale = LocationThreadLocal.getLocaleContext().locale();

        String language = locale.getLanguage();
        String country = locale.getCountry();

        return String.format("%s-%s", language, country);
    }
}
