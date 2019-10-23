package be.webfactor.c3s.content.service.contentful;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAEntry;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import be.webfactor.c3s.content.service.domain.*;
import be.webfactor.c3s.master.domain.LocationThreadLocal;

public class ContentfulContentItem implements ContentItem {

	private static final String METADATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
	private static final String EDIT_URL_PATTERN = "https://app.contentful.com/spaces/%s/entries/%s";

	private CDAEntry cdaEntry;

	ContentfulContentItem(CDAEntry cdaEntry) {
		this.cdaEntry = cdaEntry;
	}

	public String getText(String fieldName) {
		return cdaEntry.getField(getLocale(), fieldName);
	}

	public Boolean getBoolean(String fieldName) {
		return cdaEntry.getField(getLocale(), fieldName);
	}

	public RichTextField getRichText(String fieldName) {
		String value = cdaEntry.getField(getLocale(), fieldName);

		return value == null ? null : new ContentfulRichTextField(value);
	}

	public ImageField getImage(String fieldName) {
		CDAAsset asset = cdaEntry.getField(getLocale(), fieldName);

		return asset == null ? null : new ContentfulImageField(asset);
	}

	public DateField getDate(String fieldName) {
		String value = cdaEntry.getField(getLocale(), fieldName);

		return value == null ? null : new ContentfulDateField(value);
	}

	public NumberField getNumber(String fieldName) {
		Double value = cdaEntry.getField(getLocale(), fieldName);

		return value == null ? null : new ContentfulNumberField(value);
	}

	public String getWebLink(String fieldName) {
		throw new UnsupportedOperationException();
	}

	public GeolocationField getGeolocation(String fieldName) {
		LinkedTreeMap<String, Double> value = cdaEntry.getField(getLocale(), fieldName);

		return value == null ? null : new ContentfulGeolocationField(value);
	}

	public AssetLink getAsset(String fieldName) {
		CDAAsset asset = cdaEntry.getField(getLocale(), fieldName);

		return asset == null ? null : new ContentfulAssetLink(asset);
	}

	public JsonObject getJson(String fieldName) {
		LinkedTreeMap<String, Object> value = cdaEntry.getField(getLocale(), fieldName);

		return value == null ? null : new Gson().toJsonTree(value).getAsJsonObject();
	}

	public ContentItem getReference(String fieldName) {
		CDAEntry referencedEntry = cdaEntry.getField(getLocale(), fieldName);

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
		String spaceId = getAttribute(cdaEntry, "space", "sys", "id");
		String documentId = cdaEntry.id();

		return String.format(EDIT_URL_PATTERN, spaceId, documentId);
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

	private String getAttribute(CDAEntry entry, String... attributePathParts) {
		Object attribute = entry.getAttribute(attributePathParts[0]);

		for (int i = 1; i <= attributePathParts.length; i++) {
			if (attribute instanceof String) {
				return (String) attribute;
			}

			if (i < attributePathParts.length) {
				attribute = ((Map) attribute).get(attributePathParts[i]);
			}
		}

		throw new IllegalArgumentException("Attribute path does not resolve to a valid attribute");
	}

	private String getLocale() {
		return LocationThreadLocal.getLocale().toString().replace('_', '-');
	}
}