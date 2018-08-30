package be.webfactor.c3s.content.service.mock;

import com.google.gson.JsonObject;

import be.webfactor.c3s.content.service.domain.*;

public class MockFieldContainer implements FieldContainer {

	private String type;

	MockFieldContainer(String type) {
		this.type = type;
	}

	public String getText(String fieldName) {
		return type + "." + fieldName;
	}

	public Boolean getBoolean(String fieldName) {
		return MockRandomGenerator.bool();
	}

	public RichTextField getRichText(String fieldName) {
		return new MockRichTextField(type + "." + fieldName);
	}

	public ImageField getImage(String fieldName) {
		return new MockImageField();
	}

	public DateField getDate(String fieldName) {
		return new MockDateField();
	}

	public NumberField getNumber(String fieldName) {
		return new MockNumberField();
	}

	public String getWebLink(String fieldName) {
		return MockRandomGenerator.url();
	}

	public GeolocationField getGeolocation(String fieldName) {
		return new MockGeolocationField();
	}

	public AssetLink getAsset(String fieldName) {
		return new MockAssetLink(type + "." + fieldName);
	}

	public JsonObject getJson(String fieldName) {
		return MockRandomGenerator.json();
	}

	public ContentItem getReference(String fieldName) {
		return new MockContentItem(type + "." + fieldName);
	}
}
