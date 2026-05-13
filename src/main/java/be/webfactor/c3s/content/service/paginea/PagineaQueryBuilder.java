package be.webfactor.c3s.content.service.paginea;

import be.webfactor.c3s.content.service.domain.ContentItem;
import be.webfactor.c3s.content.service.domain.QueryBuilder;
import be.webfactor.c3s.content.service.paginea.api.ContentItemsApi;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PagineaQueryBuilder implements QueryBuilder {

    private final String siteKey;
    private final String contentTypeKey;
    private final ContentItemsApi api;

    private final List<String> filters = new ArrayList<>();
    private String sort;

    @Override
    public QueryBuilder with(String field, String value) {
        filters.add(field + ":" + value);
        return this;
    }

    @Override
    public QueryBuilder with(String field, ContentItem value) {
        throw new UnsupportedOperationException("Filtering by content item reference is not supported by the Paginea API");
    }

    @Override
    public QueryBuilder search(String keyword) {
        throw new UnsupportedOperationException("Keyword search is not supported by the Paginea API");
    }

    @Override
    public QueryBuilder withTag(String tag) {
        throw new UnsupportedOperationException("Tag filtering is not supported by the Paginea API");
    }

    @Override
    public QueryBuilder withDateInPast(String field) {
        throw new UnsupportedOperationException("Date filtering is not supported by the Paginea API");
    }

    @Override
    public QueryBuilder withDateBeforeToday(String field) {
        throw new UnsupportedOperationException("Date filtering is not supported by the Paginea API");
    }

    @Override
    public QueryBuilder withDateInFuture(String field) {
        throw new UnsupportedOperationException("Date filtering is not supported by the Paginea API");
    }

    @Override
    public QueryBuilder withDateAfterToday(String field) {
        throw new UnsupportedOperationException("Date filtering is not supported by the Paginea API");
    }

    @Override
    public QueryBuilder withDateToday(String field) {
        throw new UnsupportedOperationException("Date filtering is not supported by the Paginea API");
    }

    @Override
    public QueryBuilder orderByAsc(String fieldName) {
        sort = fieldName + ",asc";
        return this;
    }

    @Override
    public QueryBuilder orderByDesc(String fieldName) {
        sort = fieldName + ",desc";
        return this;
    }

    @Override
    public int count() {
        return api.getContentItemCount(siteKey, contentTypeKey, filtersOrNull()).getCount().intValue();
    }

    @Override
    public List<? extends ContentItem> findAll() {
        return getItems(null, null, null);
    }

    @Override
    public List<? extends ContentItem> findAll(int limit) {
        return getItems(null, null, limit);
    }

    @Override
    public List<? extends ContentItem> findAll(int page, int size) {
        return getItems(page - 1, size, null);
    }

    @Override
    public ContentItem findOne() {
        List<? extends ContentItem> items = findAll(1);
        return items.isEmpty() ? null : items.get(0);
    }

    @Override
    public List<? extends ContentItem> findRandom() {
        return findRandom(null);
    }

    @Override
    public List<? extends ContentItem> findRandom(int limit) {
        return findRandom(Integer.valueOf(limit));
    }

    private List<PagineaContentItem> findRandom(Integer limit) {
        sort = "random";
        return getItems(null, null, limit);
    }

    private List<PagineaContentItem> getItems(Integer page, Integer size, Integer limit) {
        return api.getContentItems(siteKey, contentTypeKey, filtersOrNull(), sort, page, size, limit).stream()
                .map(PagineaContentItem::new)
                .toList();
    }

    private List<String> filtersOrNull() {
        return filters.isEmpty() ? null : filters;
    }
}
