package be.webfactor.c3s.content.service.prismic;

import be.webfactor.c3s.content.service.domain.*;
import io.prismic.Api;
import io.prismic.Fragment;
import io.prismic.WithFragments;

public class PrismicFieldContainer implements FieldContainer {

	private WithFragments withFragments;
	protected Api api;

	PrismicFieldContainer(WithFragments withFragments, Api api) {
		this.withFragments = withFragments;
		this.api = api;
	}

	public String getText(String fieldName) {
		return withFragments.getText(fieldName);
	}

	public RichTextField getRichText(String fieldName) {
		return new PrismicRichTextField(withFragments.getStructuredText(fieldName));
	}

	public ImageField getImage(String fieldName) {
		return new PrismicImageField(withFragments.getImage(fieldName));
	}

	public DateField getDate(String fieldName) {
		return new PrismicDateField(withFragments.get(fieldName));
	}

	public NumberField getNumber(String fieldName) {
		return new PrismicNumberField(withFragments.getNumber(fieldName));
	}

	public WebLink getWebLink(String fieldName) {
		return new PrismicWebLink((Fragment.WebLink) withFragments.getLink(fieldName));
	}

	public GeolocationField getGeolocation(String fieldName) {
		return new PrismicGeolocationField(withFragments.getGeoPoint(fieldName));
	}

	public AssetLink getAsset(String fieldName) {
		return new PrismicAssetLink(withFragments.getLink(fieldName));
	}

	public ContentItem getReference(String fieldName) {
		Fragment.DocumentLink link = (Fragment.DocumentLink) withFragments.getLink(fieldName);

		return link == null ? null : new PrismicContentItem(api.getByID(link.getId()), api);
	}
}
