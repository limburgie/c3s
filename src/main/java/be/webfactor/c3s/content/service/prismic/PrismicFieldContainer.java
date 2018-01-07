package be.webfactor.c3s.content.service.prismic;

import be.webfactor.c3s.content.service.domain.*;
import io.prismic.Api;
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

	public ImageField getImage(String fieldName) {
		return new PrismicImageField(withFragments.getImage(fieldName));
	}

	public DateField getDate(String fieldName) {
		return new PrismicDateField(withFragments.get(fieldName));
	}

	public NumberField getNumber(String fieldName) {
		return new PrismicNumberField(withFragments.getNumber(fieldName));
	}

	public ReferenceField getReference(String fieldName) {
		return new PrismicReferenceField(withFragments.getLink(fieldName), api);
	}
}
