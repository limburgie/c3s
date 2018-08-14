package be.webfactor.c3s.content.service.graphcms;

import java.util.List;

import be.webfactor.c3s.content.service.domain.*;

public class GraphCmsContentItem implements ContentItem {

	private String id;

	GraphCmsContentItem(String id) {
		this.id = id;
	}

	public List<FieldContainer> getGroup(String fieldName) {
		return null;
	}

	public String getUid() {
		return null;
	}

	public String getId() {
		return id;
	}

	public DateBuilder getCreated(String pattern) {
		return null;
	}

	public DateBuilder getModified(String pattern) {
		return null;
	}

	public String getText(String fieldName) {
		return null;
	}

	public boolean getBoolean(String fieldName) {
		return false;
	}

	public RichTextField getRichText(String fieldName) {
		return null;
	}

	public ImageField getImage(String fieldName) {
		return null;
	}

	public DateField getDate(String fieldName) {
		return null;
	}

	public NumberField getNumber(String fieldName) {
		return null;
	}

	public WebLink getWebLink(String fieldName) {
		return null;
	}

	public GeolocationField getGeolocation(String fieldName) {
		return null;
	}

	public AssetLink getAsset(String fieldName) {
		return null;
	}

	public ContentItem getReference(String fieldName) {
		return null;
	}
}
