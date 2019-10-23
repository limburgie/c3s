package be.webfactor.c3s.master.domain;

import java.time.ZoneId;
import java.util.Locale;

public class LocationThreadLocal extends ThreadLocal<String> {

	private static final Locale DEFAULT_LOCALE = new Locale("en", "US");
	private static final ZoneId DEFAULT_TIME_ZONE = ZoneId.of("UTC");

	private static final ThreadLocal<Locale> LOCALE = ThreadLocal.withInitial(() -> DEFAULT_LOCALE);
	private static final ThreadLocal<ZoneId> TIME_ZONE = ThreadLocal.withInitial(() -> DEFAULT_TIME_ZONE);

	public static void setLocale(Locale locale) {
		LOCALE.set(locale);
	}

	public static Locale getLocale() {
		Locale result = LOCALE.get();

		return result == null ? DEFAULT_LOCALE : result;
	}

	public static boolean hasLocale() {
		return LOCALE.get() != null;
	}

	public static void setTimeZone(ZoneId timeZone) {
		TIME_ZONE.set(timeZone == null ? DEFAULT_TIME_ZONE : timeZone);
	}

	public static ZoneId getTimeZone() {
		return TIME_ZONE.get();
	}
}