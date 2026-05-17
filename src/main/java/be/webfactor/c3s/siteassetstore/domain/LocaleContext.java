package be.webfactor.c3s.siteassetstore.domain;

import java.util.Locale;

public record LocaleContext(Locale locale, boolean defaultLocale, boolean uriLocalePrefixed) {

    public static final Locale DEFAULT_LOCALE = new Locale("en", "US");

    public LocaleContext() {
        this(DEFAULT_LOCALE, true, false);
    }

    public LocaleContext(Locale locale) {
        this(locale, false, false);
    }

    @Override
    public Locale locale() {
        return locale == null ? DEFAULT_LOCALE : locale;
    }
}