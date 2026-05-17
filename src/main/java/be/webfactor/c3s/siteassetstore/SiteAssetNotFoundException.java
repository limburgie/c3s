package be.webfactor.c3s.siteassetstore;

public class SiteAssetNotFoundException extends RuntimeException {

	public SiteAssetNotFoundException(String message) {
		super(message);
	}

	public SiteAssetNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
