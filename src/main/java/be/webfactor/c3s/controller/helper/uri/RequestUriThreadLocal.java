package be.webfactor.c3s.controller.helper.uri;

public class RequestUriThreadLocal extends ThreadLocal<String> {

	private static final ThreadLocal<String> CURRENT_URI = ThreadLocal.withInitial(() -> null);

	public static void setCurrentUri(String currentUri) {
		CURRENT_URI.set(currentUri);
	}

	public static String getCurrentUri() {
		return CURRENT_URI.get();
	}
}