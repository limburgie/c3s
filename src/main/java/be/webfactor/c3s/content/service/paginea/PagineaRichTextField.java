package be.webfactor.c3s.content.service.paginea;

import be.webfactor.c3s.content.service.domain.RichTextField;
import be.webfactor.c3s.content.service.paginea.model.PagineaRichTextFieldDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PagineaRichTextField extends RichTextField {

	private final PagineaRichTextFieldDto richTextFieldDto;

	public String getHtml() {
		return richTextFieldDto.getTextValue();
	}
}