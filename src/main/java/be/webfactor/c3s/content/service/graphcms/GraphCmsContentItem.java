package be.webfactor.c3s.content.service.graphcms;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import be.webfactor.c3s.content.service.domain.*;

public class GraphCmsContentItem implements ContentItem {

	private static final String METADATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

	private String id;
	private String type;
	private GraphCmsClient client;

	GraphCmsContentItem(String id, String type, GraphCmsClient client) {
		this.id = id;
		this.type = type;
		this.client = client;
	}

	public List<FieldContainer> getGroup(String fieldName) {
		throw new UnsupportedOperationException();
	}

	public String getUid() {
		throw new UnsupportedOperationException();
	}

	public String getId() {
		return id;
	}

	public DateBuilder getCreated(String pattern) {
		return getMetaDate("createdAt", pattern);
	}

	public DateBuilder getModified(String pattern) {
		return getMetaDate("updatedAt", pattern);
	}

	private DateBuilder getMetaDate(String dateAttribute, String pattern) {
		return new DateBuilder(ZonedDateTime.parse(getText(dateAttribute), DateTimeFormatter.ofPattern(METADATE_PATTERN)), pattern);
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
		JsonElement element = get(fieldName);

		return element.isJsonNull() ? null : new GraphCmsDateField(element.getAsString());
	}

	public NumberField getNumber(String fieldName) {
		JsonElement element = get(fieldName);

		return element.isJsonNull() ? null : new GraphCmsNumberField(element.getAsNumber());
	}

	public String getWebLink(String fieldName) {
		throw new UnsupportedOperationException();
	}

	public GeolocationField getGeolocation(String fieldName) {
		return null;
	}

	public AssetLink getAsset(String fieldName) {
		JsonElement element = get(fieldName + " { fileName url mimeType size }", fieldName);

		return element.isJsonNull() ? null : new GraphCmsAssetLink(element.getAsJsonObject());
	}

	public JsonObject getJson(String fieldName) {
		JsonElement element = get(fieldName);

		return element.isJsonNull() ? null : element.getAsJsonObject();
	}

	public ContentItem getReference(String fieldName) {
		JsonElement element = get(fieldName + " { id }", fieldName);

		return element.isJsonNull() ? null : new GraphCmsContentItem(element.getAsJsonObject().get("id").getAsString(), type, client);
	}

	private JsonElement get(String fieldName) {
		return get(fieldName, fieldName);
	}

	private JsonElement get(String definition, String fieldName) {
		String query = "{" + type + "( where: { id: \"" + id + "\" } ) { " + definition + " } }";
		JsonObject resultObject = client.execute(query);
		JsonObject dataObject = resultObject.getAsJsonObject("data");
		JsonObject typeObject = dataObject.getAsJsonObject(type);

		return typeObject.get(fieldName);
	}
}