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
        return getFieldAs(fieldName, PagineaFieldDto.class).map(PagineaFieldDto::getTextValue).orElse(null);
    }

    @Override
    public Boolean getBoolean(String fieldName) {
        return getFieldAs(fieldName, PagineaBooleanFieldDto.class).map(PagineaBooleanFieldDto::getBooleanValue).orElse(null);
    }

    @Override
    public RichTextField getRichText(String fieldName) {
        return getFieldAs(fieldName, PagineaRichTextFieldDto.class).map(PagineaRichTextField::new).orElse(null);
    }

    @Override
    public ImageField getImage(String fieldName) {
        return getFieldAs(fieldName, PagineaImageFieldDto.class).map(PagineaImageField::new).orElse(null);
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
        return getFieldAs(fieldName, PagineaMediaLinkFieldDto.class).map(PagineaAssetLink::new).orElse(null);
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
                .filter(field -> fieldClass.isAssignableFrom(field.getClass()))
                .map(fieldClass::cast);
    }
}
