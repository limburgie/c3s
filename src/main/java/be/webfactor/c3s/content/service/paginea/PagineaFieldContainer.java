package be.webfactor.c3s.content.service.paginea;

import be.webfactor.c3s.content.service.domain.*;
import be.webfactor.c3s.content.service.paginea.model.*;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class PagineaFieldContainer implements FieldContainer {

    private final Map<String, PagineaFieldDto> fields;

    @Override
    public String getText(String fieldName) {
        return getFieldAs(fieldName, PagineaTextFieldDto.class).map(PagineaFieldDto::getTextValue).orElse(null);
    }

    @Override
    public Boolean getBoolean(String fieldName) {
        return getFieldAs(fieldName, PagineaBooleanFieldDto.class).map(PagineaBooleanFieldDto::getBooleanValue).orElse(null);
    }

    @Override
    public RichTextField getRichText(String fieldName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImageField getImage(String fieldName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DateField getDate(String fieldName) {
        return getFieldAs(fieldName, PagineaDateFieldDto.class).map(PagineaDateField::new).orElse(null);
    }

    @Override
    public NumberField getNumber(String fieldName) {
        return getFieldAs(fieldName, PagineaNumberFieldDto.class).map(PagineaNumberField::new).orElse(null);
    }

    @Override
    public String getWebLink(String fieldName) {
        return getText(fieldName);
    }

    @Override
    public GeolocationField getGeolocation(String fieldName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AssetLink getAsset(String fieldName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonObject getJson(String fieldName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ContentItem getReference(String fieldName) {
        throw new UnsupportedOperationException();
    }

    protected <T extends PagineaFieldDto> Optional<T> getFieldAs(String fieldName, Class<T> fieldClass) {
        return Optional.ofNullable(fields.get(fieldName))
                .filter(field -> field.getClass().isAssignableFrom(fieldClass))
                .map(fieldClass::cast);
    }
}
