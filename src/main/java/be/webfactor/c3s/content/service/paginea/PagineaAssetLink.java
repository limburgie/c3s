package be.webfactor.c3s.content.service.paginea;

import be.webfactor.c3s.content.service.domain.AssetLink;
import be.webfactor.c3s.content.service.paginea.model.PagineaMediaLinkFieldDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;

@RequiredArgsConstructor
public class PagineaAssetLink implements AssetLink {

	private final PagineaMediaLinkFieldDto mediaLinkFieldDto;

	@Override
	public String getUrl() {
		return mediaLinkFieldDto.getUrl();
	}

	@Override
	public String getFilename() {
		return mediaLinkFieldDto.getName();
	}

	@Override
	public String getExtension() {
		return FilenameUtils.getExtension(getFilename());
	}

	@Override
	public String getType() {
		return mediaLinkFieldDto.getMimeType();
	}

	@Override
	public Long getSize() {
		return mediaLinkFieldDto.getFileSize();
	}
}