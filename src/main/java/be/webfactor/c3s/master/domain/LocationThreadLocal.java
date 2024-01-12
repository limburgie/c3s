package be.webfactor.c3s.master.domain;

import java.time.ZoneId;

public class LocationThreadLocal extends ThreadLocal<String> {

	private static final ZoneId DEFAULT_TIME_ZONE = ZoneId.of("UTC");

	private static final ThreadLocal<LocaleContext> LOCALE_CONTEXT = ThreadLocal.withInitial(LocaleContext::new);
	private static final ThreadLocal<ZoneId> TIME_ZONE = ThreadLocal.withInitial(() -> DEFAULT_TIME_ZONE);

	public static void setLocaleContext(LocaleContext localeContext) {
		LOCALE_CONTEXT.set(localeContext);
	}

	public static LocaleContext getLocaleContext() {
		return LOCALE_CONTEXT.get();
	}

	public static void setTimeZone(ZoneId timeZone) {
		TIME_ZONE.set(timeZone == null ? DEFAULT_TIME_ZONE : timeZone);
	}

	public static ZoneId getTimeZone() {
		return TIME_ZONE.get();
	}
}