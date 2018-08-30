package be.webfactor.c3s.content.service.mock;

import be.webfactor.c3s.content.service.domain.GeolocationField;

public class MockGeolocationField implements GeolocationField {

	public Double getLatitude() {
		return MockRandomGenerator.number();
	}

	public Double getLongitude() {
		return MockRandomGenerator.number();
	}
}