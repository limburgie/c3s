package be.webfactor.c3s.master.domain;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Locale;

@Value
@AllArgsConstructor
public class LocaleContext {

    public static final Locale DEFAULT_LOCALE = new Locale("en", "US");

    Locale locale;
    boolean uriLocalePrefixed;

    public LocaleContext() {
        this(DEFAULT_LOCALE, false);
    }

    public Locale getLocale() {
        return locale == null ? DEFAULT_LOCALE : locale;
    }
}