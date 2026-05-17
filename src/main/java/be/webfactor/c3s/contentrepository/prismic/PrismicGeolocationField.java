package be.webfactor.c3s.contentrepository.prismic;

import be.webfactor.c3s.contentrepository.domain.GeolocationField;
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