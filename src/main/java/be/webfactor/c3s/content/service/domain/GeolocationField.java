package be.webfactor.c3s.content.service.domain;

public interface GeolocationField extends ValueWrapper {

	/**
	 * Gets the latitude of this geolocation.
	 */
	Double getLatitude();

	/**
	 * Gets the longitude of this geolocation.
	 */
	Double getLongitude();
}
