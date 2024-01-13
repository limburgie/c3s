package be.webfactor.c3s.renderer;

import be.webfactor.c3s.master.domain.LocationThreadLocal;
import be.webfactor.c3s.master.domain.Page;

public class UriHelper {

    public String ofIndex() {
        return ofIndex(LocationThreadLocal.getLocaleContext().getLocale().getLanguage());
    }

    public String ofIndex(String language) {
        StringBuilder sb = new StringBuilder("/");

        if (be.webfactor.c3s.master.domain.LocationThreadLocal.getLocaleContext().isUriLocalePrefixed()) {
            sb.append(language);
        }

        return sb.toString();
    }

    public String of(Page page) {
        return of(page, LocationThreadLocal.getLocaleContext().getLocale().getLanguage());
    }

    public String of(Page page, String language) {
        StringBuilder sb = new StringBuilder("/");

        if (LocationThreadLocal.getLocaleContext().isUriLocalePrefixed()) {
            sb.append(language).append("/");
        }

        sb.append(page.getFriendlyUrl());

        return sb.toString();
    }
}
