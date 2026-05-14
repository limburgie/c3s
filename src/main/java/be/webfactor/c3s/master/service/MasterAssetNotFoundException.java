package be.webfactor.c3s.master.service;

public class MasterAssetNotFoundException extends RuntimeException {

	public MasterAssetNotFoundException(String message) {
		super(message);
	}

	public MasterAssetNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
