package be.webfactor.c3s.content.service.prismic;

import be.webfactor.c3s.content.service.domain.GeolocationField;
import io.prismic.Fragment;

public class PrismicGeolocationField implements GeolocationField {

	private Fragment.GeoPoint geoPoint;

	PrismicGeolocationField(Fragment.GeoPoint geoPoint) {
		this.geoPoint = geoPoint;
	}

	public Double getLatitude() {
		return geoPoint == null ? null : geoPoint.getLatitude();
	}

	public Double getLongitude() {
		return geoPoint == null ? null : geoPoint.getLongitude();
	}

	public boolean isEmpty() {
		return geoPoint == null;
	}
}
