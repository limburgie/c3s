package be.webfactor.c3s.content.service.paginea;

import be.webfactor.c3s.content.service.domain.*;
import be.webfactor.c3s.content.service.paginea.model.PagineaContentItemDto;
import be.webfactor.c3s.content.service.paginea.model.PagineaGroupFieldDto;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public class PagineaContentItem extends PagineaFieldContainer implements ContentItem {

    private final PagineaContentItemDto item;
    
    public PagineaContentItem(PagineaContentItemDto item) {
        super(item.getFields());
        this.item = item;
    }

    @Override
    public List<FieldContainer> getGroup(String fieldName) {
        return getFieldAs(fieldName, PagineaGroupFieldDto.class)
                .map(group -> group.getFields().stream()
                        .map(field -> (FieldContainer) new PagineaFieldContainer(field))
                        .collect(Collectors.toList()))
                .orElse(null);
    }

    @Override
    public String getUid() {
        return item.getId();
    }

    @Override
    public String getId() {
        return item.getId();
    }

    @Override
    public String getEditUrl() {
        return item.getEditUrl();
    }

    @Override
    public DateBuilder getCreated(String pattern) {
        return new DateBuilder(item.getCreatedAt(), pattern);
    }

    @Override
    public DateBuilder getModified(String pattern) {
        return new DateBuilder(item.getLastModifiedAt(), pattern);
    }

    @Override
    public String getText(String fieldName) {
        return super.getText(fieldName);
    }

    @Override
    public Boolean getBoolean(String fieldName) {
        return super.getBoolean(fieldName);
    }

    @Override
    public RichTextField getRichText(String fieldName) {
        return super.getRichText(fieldName);
    }

    @Override
    public ImageField getImage(String fieldName) {
        return super.getImage(fieldName);
    }

    @Override
    public DateField getDate(String fieldName) {
        return super.getDate(fieldName);
    }

    @Override
    public NumberField getNumber(String fieldName) {
        return super.getNumber(fieldName);
    }

    @Override
    public String getWebLink(String fieldName) {
        return super.getWebLink(fieldName);
    }

    @Override
    public GeolocationField getGeolocation(String fieldName) {
        return super.getGeolocation(fieldName);
    }

    @Override
    public AssetLink getAsset(String fieldName) {
        return super.getAsset(fieldName);
    }

    @Override
    public JsonObject getJson(String fieldName) {
        return super.getJson(fieldName);
    }

    @Override
    public ContentItem getReference(String fieldName) {
        return super.getReference(fieldName);
    }
}
