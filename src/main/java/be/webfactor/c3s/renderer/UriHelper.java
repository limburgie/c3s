package be.webfactor.c3s.renderer;

import be.webfactor.c3s.controller.helper.uri.RequestUriThreadLocal;
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

    public String ofCurrent() {
        return of(RequestUriThreadLocal.getCurrentUri());
    }

    public String ofCurrent(String language) {
        return of(RequestUriThreadLocal.getCurrentUri(), language);
    }

    public String of(Page page) {
        return of(page, LocationThreadLocal.getLocaleContext().getLocale().getLanguage());
    }

    public String of(Page page, String language) {
        return of(page.getFriendlyUrl(), language);
    }

    public String of(String friendlyUrl) {
        return of(friendlyUrl, LocationThreadLocal.getLocaleContext().getLocale().getLanguage());
    }

    private String of(String friendlyUrl, String language) {
        StringBuilder sb = new StringBuilder("/");

        if (LocationThreadLocal.getLocaleContext().isUriLocalePrefixed()) {
            sb.append(language).append("/");
        }

        sb.append(friendlyUrl);

        return sb.toString();
    }
}
