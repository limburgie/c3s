package be.webfactor.c3s.content.service.paginea;

import be.webfactor.c3s.content.service.domain.ContentItem;
import be.webfactor.c3s.content.service.domain.QueryBuilder;
import be.webfactor.c3s.content.service.paginea.api.ContentItemsApi;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PagineaQueryBuilder implements QueryBuilder {

    private final String siteKey;
    private final String contentTypeKey;
    private final ContentItemsApi api;

    @Override
    public QueryBuilder with(String field, String value) {
        return null;
    }

    @Override
    public QueryBuilder with(String field, ContentItem value) {
        return null;
    }

    @Override
    public QueryBuilder search(String keyword) {
        return null;
    }

    @Override
    public QueryBuilder withTag(String tag) {
        return null;
    }

    @Override
    public QueryBuilder withDateInPast(String field) {
        return null;
    }

    @Override
    public QueryBuilder withDateBeforeToday(String field) {
        return null;
    }

    @Override
    public QueryBuilder withDateInFuture(String field) {
        return null;
    }

    @Override
    public QueryBuilder withDateAfterToday(String field) {
        return null;
    }

    @Override
    public QueryBuilder withDateToday(String field) {
        return null;
    }

    @Override
    public QueryBuilder orderByAsc(String fieldName) {
        return null;
    }

    @Override
    public QueryBuilder orderByDesc(String fieldName) {
        return null;
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public List<? extends ContentItem> findAll() {
        return api.getContentItems(siteKey, contentTypeKey).stream()
                .map(PagineaContentItem::new)
                .toList();
    }

    @Override
    public List<? extends ContentItem> findAll(int limit) {
        return List.of();
    }

    @Override
    public List<? extends ContentItem> findAll(int page, int size) {
        return List.of();
    }

    @Override
    public ContentItem findOne() {
        return null;
    }

    @Override
    public List<? extends ContentItem> findRandom() {
        return List.of();
    }

    @Override
    public List<? extends ContentItem> findRandom(int limit) {
        return List.of();
    }
}
