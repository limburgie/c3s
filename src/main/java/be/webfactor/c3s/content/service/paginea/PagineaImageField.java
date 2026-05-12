package be.webfactor.c3s.content.service.paginea;

import be.webfactor.c3s.content.service.domain.ImageField;
import be.webfactor.c3s.content.service.paginea.model.PagineaImageFieldDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PagineaImageField implements ImageField {

	private final PagineaImageFieldDto imageFieldDto;

	@Override
	public String getUrl() {
		return imageFieldDto.getUrl();
	}

	@Override
	public String getFinalUrl() {
		return imageFieldDto.getUrl();
	}

	@Override
	public String getAlt() {
		return imageFieldDto.getAlt();
	}
}