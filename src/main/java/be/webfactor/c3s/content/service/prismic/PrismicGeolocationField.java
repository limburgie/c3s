package be.webfactor.c3s.content.service.prismic;

import be.webfactor.c3s.content.service.domain.GeolocationField;
import io.prismic.Fragment;

public class PrismicGeolocationField implements GeolocationField {

	private Fragment.GeoPoint geoPoint;

	PrismicGeolocationField(Fragment.GeoPoint geoPoint) {
		this.geoPoint = geoPoint;
	}

	public Double getLatitude() {
		return geoPoint.getLatitude();
	}

	public Double getLongitude() {
		return geoPoint.getLongitude();
	}
}