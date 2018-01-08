package be.webfactor.c3s.content.service.contentful;

import com.google.gson.internal.LinkedTreeMap;

import be.webfactor.c3s.content.service.domain.GeolocationField;

public class ContentfulGeolocationField implements GeolocationField {

	private LinkedTreeMap<String, Double> coordinates;

	ContentfulGeolocationField(LinkedTreeMap<String, Double> coordinates) {
		this.coordinates = coordinates;
	}

	public Double getLatitude() {
		return coordinates == null ? null : coordinates.get("lat");
	}

	public Double getLongitude() {
		return coordinates == null ? null : coordinates.get("lon");
	}
}
