package be.webfactor.c3s.content.service.contentful;

import java.util.List;

import com.contentful.java.cda.CDAEntry;

import be.webfactor.c3s.content.service.domain.*;

public class ContentfulContentItem implements ContentItem {

	private CDAEntry cdaEntry;

	ContentfulContentItem(CDAEntry cdaEntry) {
		this.cdaEntry = cdaEntry;
	}

	public String getText(String fieldName) {
		String text = cdaEntry.getField(fieldName);

		return text == null ? "" : text;
	}

	public ImageField getImage(String fieldName) {
		return new ContentfulImageField(cdaEntry.getField(fieldName));
	}

	public DateField getDate(String fieldName) {
		return new ContentfulDateField(cdaEntry.getField(fieldName));
	}

	public NumberField getNumber(String fieldName) {
		return new ContentfulNumberField(cdaEntry.getField(fieldName));
	}

	public ContentItem getReference(String fieldName) {
		throw new UnsupportedOperationException();
	}

	public List<FieldContainer> getGroup(String fieldName) {
		throw new UnsupportedOperationException();
	}
}
