package be.webfactor.c3s.renderer;

import be.webfactor.c3s.master.domain.LocaleContext;
import be.webfactor.c3s.master.domain.LocationThreadLocal;
import be.webfactor.c3s.master.domain.Page;

public class UriHelper {

    public String ofIndex() {
        LocaleContext localeContext = LocationThreadLocal.getLocaleContext();

        StringBuilder sb = new StringBuilder("/");

        if (localeContext.isUriLocalePrefixed()) {
            sb.append(localeContext.getLocale().getLanguage());
        }

        return sb.toString();
    }

    public String of(Page page) {
        LocaleContext localeContext = LocationThreadLocal.getLocaleContext();

        StringBuilder sb = new StringBuilder("/");

        if (localeContext.isUriLocalePrefixed()) {
            sb.append(localeContext.getLocale().getLanguage()).append("/");
        }

        sb.append(page.getFriendlyUrl());

        return sb.toString();
    }
}
