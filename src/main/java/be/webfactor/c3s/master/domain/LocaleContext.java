package be.webfactor.c3s.master.domain;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Locale;

@Value
@AllArgsConstructor
public class LocaleContext {

    public static final Locale DEFAULT_LOCALE = new Locale("en", "US");

    Locale locale;
    boolean defaultLocale;
    boolean uriLocalePrefixed;

    public LocaleContext() {
        this(DEFAULT_LOCALE, true, false);
    }

    public LocaleContext(Locale locale) {
        this(locale, false, false);
    }

    public Locale getLocale() {
        return locale == null ? DEFAULT_LOCALE : locale;
    }
}