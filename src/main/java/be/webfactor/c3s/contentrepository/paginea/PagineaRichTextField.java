package be.webfactor.c3s.contentrepository.paginea;

import be.webfactor.c3s.contentrepository.domain.RichTextField;
import be.webfactor.c3s.contentrepository.paginea.model.PagineaRichTextFieldDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PagineaRichTextField extends RichTextField {

	private final PagineaRichTextFieldDto richTextFieldDto;

	public String getHtml() {
		return richTextFieldDto.getTextValue();
	}
}