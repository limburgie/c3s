package be.webfactor.c3s.contentrepository.paginea;

import be.webfactor.c3s.contentrepository.domain.ImageField;
import be.webfactor.c3s.contentrepository.paginea.model.PagineaImageFieldDto;
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