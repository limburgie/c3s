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

	public RichTextField getRichText(String fieldName) {
		return new ContentfulRichTextField(cdaEntry.getField(fieldName));
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

	public WebLink getWebLink(String fieldName) {
		throw new UnsupportedOperationException();
	}

	public GeolocationField getGeolocation(String fieldName) {
		return new ContentfulGeolocationField(cdaEntry.getField(fieldName));
	}

	public AssetLink getAsset(String fieldName) {
		return new ContentfulAssetLink(cdaEntry.getField(fieldName));
	}

	public ContentItem getReference(String fieldName) {
		CDAEntry referencedEntry = cdaEntry.getField(fieldName);

		return referencedEntry == null ? null : new ContentfulContentItem(referencedEntry);
	}

	public List<FieldContainer> getGroup(String fieldName) {
		throw new UnsupportedOperationException();
	}
}
