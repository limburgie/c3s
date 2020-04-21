package be.webfactor.c3s.content.service.prismic;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import be.webfactor.c3s.content.service.domain.*;
import io.prismic.Api;
import io.prismic.Document;
import io.prismic.Fragment;

public class PrismicContentItem extends PrismicFieldContainer implements ContentItem {

	private static final String EDIT_URL_PATTERN = "https://%s/app/documents/%s/ref";

	private Document document;

	PrismicContentItem(Document document, Api api) {
		super(document, api);

		this.document = document;
	}

	public String getText(String fieldName) {
		return super.getText(docPrefix(fieldName));
	}

	public Boolean getBoolean(String fieldName) {
		return super.getBoolean(docPrefix(fieldName));
	}

	public RichTextField getRichText(String fieldName) {
		return super.getRichText(docPrefix(fieldName));
	}

	public ImageField getImage(String fieldName) {
		return super.getImage(docPrefix(fieldName));
	}

	public DateField getDate(String fieldName) {
		return super.getDate(docPrefix(fieldName));
	}

	public NumberField getNumber(String fieldName) {
		return super.getNumber(docPrefix(fieldName));
	}

	public String getWebLink(String fieldName) {
		return super.getWebLink(docPrefix(fieldName));
	}

	public GeolocationField getGeolocation(String fieldName) {
		return super.getGeolocation(docPrefix(fieldName));
	}

	public AssetLink getAsset(String fieldName) {
		return super.getAsset(docPrefix(fieldName));
	}

	public ContentItem getReference(String fieldName) {
		return super.getReference(docPrefix(fieldName));
	}

	private String docPrefix(String fieldName) {
		return document.getType() + "." + fieldName;
	}

	public List<FieldContainer> getGroup(String fieldName) {
		Fragment.Group group = document.getGroup(docPrefix(fieldName));

		return group == null ? Collections.emptyList() : group.getDocs().stream().map(withFragments -> new PrismicFieldContainer(withFragments, api)).collect(Collectors.toList());
	}

	public String getUid() {
		return "null".equals(document.getUid()) ? null : document.getUid();
	}

	public String getId() {
		return document.getId();
	}

	public String getEditUrl() {
		try {
			URI hrefUri = new URI(document.getHref());
			String cleanHost = StringUtils.remove(StringUtils.remove(hrefUri.getHost(), ".cdn"), "/api");
			return String.format(EDIT_URL_PATTERN, cleanHost, document.getId());
		} catch (URISyntaxException e) {
			throw new RuntimeException("Problem while generating edit URL for document " + document.getId(), e);
		}
	}

	public DateBuilder getCreated(String pattern) {
		return new DateBuilder(document.getFirstPublicationDate(), pattern);
	}

	public DateBuilder getModified(String pattern) {
		return new DateBuilder(document.getLastPublicationDate(), pattern);
	}
}