package be.webfactor.c3s.contentrepository.paginea;

import be.webfactor.c3s.contentrepository.domain.*;
import be.webfactor.c3s.contentrepository.paginea.model.PagineaFieldDto;

import java.util.Collections;

public class PagineaMixedContentItem implements MixedContentItem {

    private final PagineaFieldDto field;
    private final PagineaFieldContainer container;

    public PagineaMixedContentItem(PagineaFieldDto field) {
        this.field = field;
        this.container = new PagineaFieldContainer(Collections.singletonMap(field.getKey(), field));
    }

    @Override
    public String getKey() {
        return field.getKey();
    }

    @Override
    public String getType() {
        return field.getType();
    }

    @Override
    public String getText() {
        return container.getText(field.getKey());
    }

    @Override
    public RichTextField getRichText() {
        return container.getRichText(field.getKey());
    }

    @Override
    public ImageField getImage() {
        return container.getImage(field.getKey());
    }

    @Override
    public NumberField getNumber() {
        return container.getNumber(field.getKey());
    }

    @Override
    public Boolean getBoolean() {
        return container.getBoolean(field.getKey());
    }

    @Override
    public DateField getDate() {
        return container.getDate(field.getKey());
    }

    @Override
    public GeolocationField getGeolocation() {
        return container.getGeolocation(field.getKey());
    }

    @Override
    public AssetLink getAsset() {
        return container.getAsset(field.getKey());
    }
}
