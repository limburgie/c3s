package be.webfactor.c3s.content.service.graphcms;

import com.google.gson.JsonObject;

import be.webfactor.c3s.content.service.domain.AssetLink;

public class GraphCmsAssetLink implements AssetLink {

	private JsonObject jsonObject;

	GraphCmsAssetLink(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public String getUrl() {
		return jsonObject.get("url").getAsString();
	}

	public String getFilename() {
		return jsonObject.get("fileName").getAsString();
	}

	public String getExtension() {
		return getFilename().substring(getFilename().lastIndexOf('.') + 1);
	}

	public String getType() {
		return jsonObject.get("mimeType").getAsString();
	}

	public Long getSize() {
		return jsonObject.get("size").getAsLong();
	}
}
