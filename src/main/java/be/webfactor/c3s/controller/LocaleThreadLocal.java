package be.webfactor.c3s.controller;

import java.util.Locale;

public class LocaleThreadLocal {

	private static ThreadLocal<Locale> localeThreadLocal = new ThreadLocal<>();

	public static Locale get() {
		return localeThreadLocal.get();
	}

	public static void set(Locale locale) {
		localeThreadLocal.set(locale);
	}
}
