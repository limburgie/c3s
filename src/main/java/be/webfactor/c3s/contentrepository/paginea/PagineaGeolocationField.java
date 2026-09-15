package be.webfactor.c3s.contentrepository.paginea;

import be.webfactor.c3s.contentrepository.domain.GeolocationField;
import be.webfactor.c3s.contentrepository.paginea.model.PagineaGeolocationFieldDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PagineaGeolocationField implements GeolocationField {

	private final PagineaGeolocationFieldDto geolocationFieldDto;

	@Override
	public Double getLatitude() {
		return geolocationFieldDto.getLatitude();
	}

	@Override
	public Double getLongitude() {
		return geolocationFieldDto.getLongitude();
	}
}
