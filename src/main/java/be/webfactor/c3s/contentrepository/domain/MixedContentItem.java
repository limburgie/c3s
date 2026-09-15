package be.webfactor.c3s.contentrepository.domain;

/**
 * A single item inside a {@link MixedContentField}. Each item is one keyed field: templates branch on
 * {@link #getKey()} to decide how to render it, then read the value through the matching typed getter,
 * e.g. {@code <#if item.key == "image">${item.image.finalUrl}</#if>}.
 * <p>
 * A getter that does not match the item's type returns {@code null}. An item can never itself be a group
 * or a mixed content field, so no accessors for those are exposed.
 */
public interface MixedContentItem {

	/**
	 * Returns the internal field name (key) of this item, used to decide how to render it.
	 */
	String getKey();

	/**
	 * Returns the type of this item (e.g. "image", "rich-text", "number").
	 */
	String getType();

	/**
	 * Returns this item's textual value, or null if it is not a text item.
	 */
	String getText();

	/**
	 * Returns this item as a rich text field, or null if it is not a rich text item.
	 */
	RichTextField getRichText();

	/**
	 * Returns this item as an image field, or null if it is not an image item.
	 */
	ImageField getImage();

	/**
	 * Returns this item as a number field, or null if it is not a number item.
	 */
	NumberField getNumber();

	/**
	 * Returns this item's boolean value, or null if it is not a boolean item.
	 */
	Boolean getBoolean();

	/**
	 * Returns this item as a date field, or null if it is not a date item.
	 */
	DateField getDate();

	/**
	 * Returns this item as a geolocation field, or null if it is not a geolocation item.
	 */
	GeolocationField getGeolocation();

	/**
	 * Returns this item as an asset link, or null if it is not a media link item.
	 */
	AssetLink getAsset();
}
