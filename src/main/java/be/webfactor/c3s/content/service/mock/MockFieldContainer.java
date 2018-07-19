package be.webfactor.c3s.content.service.mock;

import be.webfactor.c3s.content.service.domain.*;

public class MockFieldContainer implements FieldContainer {

	private String type;

	MockFieldContainer(String type) {
		this.type = type;
	}

	public String getText(String fieldName) {
		return type + "." + fieldName;
	}

	public boolean getBoolean(String fieldName) {
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

	public WebLink getWebLink(String fieldName) {
		return new MockWebLink();
	}

	public GeolocationField getGeolocation(String fieldName) {
		return new MockGeolocationField();
	}

	public AssetLink getAsset(String fieldName) {
		return new MockAssetLink(type + "." + fieldName);
	}

	public ContentItem getReference(String fieldName) {
		return new MockContentItem(type + "." + fieldName);
	}
}
