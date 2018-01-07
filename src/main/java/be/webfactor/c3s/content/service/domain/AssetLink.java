package be.webfactor.c3s.content.service.domain;

public interface AssetLink {

	/**
	 * Returns the URL of the asset.
	 */
	String getUrl();

	/**
	 * Returns the asset's filename.
	 */
	String getFilename();

	/**
	 * Return the asset's file extension.
	 */
	String getExtension();

	/**
	 * Returns the asset's file type.
	 */
	String getType();

	/**
	 * Retruns the asset's file size.
	 */
	Long getSize();
}
