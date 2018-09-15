package be.webfactor.c3s.content.service.contentful;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAEntry;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import be.webfactor.c3s.content.service.domain.*;

public class ContentfulContentItem implements ContentItem {

	private static final String METADATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

	private CDAEntry cdaEntry;

	ContentfulContentItem(CDAEntry cdaEntry) {
		this.cdaEntry = cdaEntry;
	}

	public String getText(String fieldName) {
		return cdaEntry.getField(fieldName);
	}

	public Boolean getBoolean(String fieldName) {
		return cdaEntry.getField(fieldName);
	}

	public RichTextField getRichText(String fieldName) {
		String value = cdaEntry.getField(fieldName);

		return value == null ? null : new ContentfulRichTextField(value);
	}

	public ImageField getImage(String fieldName) {
		CDAAsset asset = cdaEntry.getField(fieldName);

		return asset == null ? null : new ContentfulImageField(asset);
	}

	public DateField getDate(String fieldName) {
		String value = cdaEntry.getField(fieldName);

		return value == null ? null : new ContentfulDateField(value);
	}

	public NumberField getNumber(String fieldName) {
		Double value = cdaEntry.getField(fieldName);

		return value == null ? null : new ContentfulNumberField(value);
	}

	public String getWebLink(String fieldName) {
		throw new UnsupportedOperationException();
	}

	public GeolocationField getGeolocation(String fieldName) {
		LinkedTreeMap<String, Double> value = cdaEntry.getField(fieldName);

		return value == null ? null : new ContentfulGeolocationField(value);
	}

	public AssetLink getAsset(String fieldName) {
		CDAAsset asset = cdaEntry.getField(fieldName);

		return asset == null ? null : new ContentfulAssetLink(asset);
	}

	public JsonObject getJson(String fieldName) {
		LinkedTreeMap<String, Object> value = cdaEntry.getField(fieldName);

		return value == null ? null : new Gson().toJsonTree(value).getAsJsonObject();
	}

	public ContentItem getReference(String fieldName) {
		CDAEntry referencedEntry = cdaEntry.getField(fieldName);

		return referencedEntry == null ? null : new ContentfulContentItem(referencedEntry);
	}

	public List<FieldContainer> getGroup(String fieldName) {
		throw new UnsupportedOperationException();
	}

	public String getUid() {
		throw new UnsupportedOperationException();
	}

	public String getId() {
		return cdaEntry.id();
	}

	public String getEditUrl() {
		throw new UnsupportedOperationException();
	}

	public DateBuilder getCreated(String pattern) {
		return getMetaDate("createdAt", pattern);
	}

	public DateBuilder getModified(String pattern) {
		return getMetaDate("updatedAt", pattern);
	}

	private DateBuilder getMetaDate(String dateAttribute, String pattern) {
		return new DateBuilder(ZonedDateTime.parse((String) cdaEntry.attrs().get(dateAttribute), DateTimeFormatter.ofPattern(METADATE_PATTERN)), pattern);
	}
}