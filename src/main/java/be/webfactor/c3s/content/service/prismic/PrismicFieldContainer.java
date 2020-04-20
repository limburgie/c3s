package be.webfactor.c3s.content.service.prismic;

import com.google.gson.JsonObject;

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
		Fragment fragment = withFragments.get(fieldName);

		return fragment == null ? null : withFragments.getText(fieldName);
	}

	public Boolean getBoolean(String fieldName) {
		Fragment fragment = withFragments.get(fieldName);

		return fragment == null ? null : ((Fragment.Raw) fragment).getValue().asBoolean();
	}

	public RichTextField getRichText(String fieldName) {
		Fragment.StructuredText value = withFragments.getStructuredText(fieldName);

		return value == null ? null : new PrismicRichTextField(value);
	}

	public ImageField getImage(String fieldName) {
		Fragment.Image image = withFragments.getImage(fieldName);

		return image == null ? null : new PrismicImageField(image);
	}

	public DateField getDate(String fieldName) {
		Fragment fragment = withFragments.get(fieldName);

		return fragment == null ? null : new PrismicDateField(fragment);
	}

	public NumberField getNumber(String fieldName) {
		Fragment.Number number = withFragments.getNumber(fieldName);

		return number == null ? null : new PrismicNumberField(number);
	}

	public String getWebLink(String fieldName) {
		Fragment.Link link = withFragments.getLink(fieldName);

		return link == null ? null : link.getUrl(null);
	}

	public GeolocationField getGeolocation(String fieldName) {
		Fragment.GeoPoint geoPoint = withFragments.getGeoPoint(fieldName);

		return geoPoint == null ? null : new PrismicGeolocationField(geoPoint);
	}

	public AssetLink getAsset(String fieldName) {
		Fragment.Link link = withFragments.getLink(fieldName);

		return link == null ? null : new PrismicAssetLink(link);
	}

	public JsonObject getJson(String fieldName) {
		throw new UnsupportedOperationException();
	}

	public ContentItem getReference(String fieldName) {
		Fragment.DocumentLink link = (Fragment.DocumentLink) withFragments.getLink(fieldName);

		return link == null ? null : new PrismicContentItem(api.getByID(link.getId()), api);
	}
}