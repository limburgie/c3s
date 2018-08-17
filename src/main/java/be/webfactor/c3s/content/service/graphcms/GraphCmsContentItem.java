package be.webfactor.c3s.content.service.graphcms;

import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import be.webfactor.c3s.content.service.domain.*;

public class GraphCmsContentItem implements ContentItem {

	private String id;
	private String type;
	private GraphCmsClient client;

	GraphCmsContentItem(String id, String type, GraphCmsClient client) {
		this.id = id;
		this.type = type;
		this.client = client;
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
		JsonElement element = get(fieldName);

		return element.isJsonNull() ? null : element.getAsString();
	}

	public Boolean getBoolean(String fieldName) {
		JsonElement element = get(fieldName);

		return element.isJsonNull() ? null : element.getAsBoolean();
	}

	public RichTextField getRichText(String fieldName) {
		JsonElement element = get(fieldName);

		return element.isJsonNull() ? null : new GraphCmsRichTextField(element.getAsString());
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

	public String getWebLink(String fieldName) {
		throw new UnsupportedOperationException();
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

	private JsonElement get(String fieldName) {
		String query = "{" + type + "( where: { id: \"" + id + "\" } ) { " + fieldName + " } }";
		JsonObject resultObject = client.execute(query);
		JsonObject dataObject = resultObject.getAsJsonObject("data");
		JsonObject typeObject = dataObject.getAsJsonObject(type);

		return typeObject.get(fieldName);
	}
}