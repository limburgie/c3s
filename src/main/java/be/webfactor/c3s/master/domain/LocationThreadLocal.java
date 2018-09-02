package be.webfactor.c3s.master.domain;

public class LocationThreadLocal extends ThreadLocal<String> {

	private static final String DEFAULT_LOCALE = "en_US";
	private static final String DEFAULT_TIME_ZONE = "UTC";

	private static final ThreadLocal<String> LOCALE = ThreadLocal.withInitial(() -> DEFAULT_LOCALE);
	private static final ThreadLocal<String> TIME_ZONE = ThreadLocal.withInitial(() -> DEFAULT_TIME_ZONE);

	public static void setLocale(String locale) {
		LOCALE.set(locale == null ? DEFAULT_LOCALE : locale);
	}

	public static String getLocale() {
		return LOCALE.get();
	}

	public static void setTimeZone(String timeZone) {
		TIME_ZONE.set(timeZone == null ? DEFAULT_TIME_ZONE : timeZone);
	}

	public static String getTimeZone() {
		return TIME_ZONE.get();
	}
}