package be.webfactor.c3s.contentrepository.mock;

import be.webfactor.c3s.contentrepository.domain.GeolocationField;

public class MockGeolocationField implements GeolocationField {

	public Double getLatitude() {
		return MockRandomGenerator.number();
	}

	public Double getLongitude() {
		return MockRandomGenerator.number();
	}
}