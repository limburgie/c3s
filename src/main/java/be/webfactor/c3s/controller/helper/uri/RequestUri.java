package be.webfactor.c3s.controller.helper.uri;

import be.webfactor.c3s.master.domain.LocaleContext;
import be.webfactor.c3s.master.domain.Page;
import be.webfactor.c3s.master.service.MasterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestUri {

    private final String path;
    private final List<Locale> supportedLocales;
    private final Page indexPage;
    private Locale requestLocale;
    private boolean hasLocalePrefix;

    @Getter
    private final LocaleContext localeContext;

    public RequestUri(HttpServletRequest request, MasterService masterService) {
        path = ((String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).substring(1);
        supportedLocales = masterService.getLocales();
        indexPage = masterService.getIndexPage();

        initLocale();

        localeContext = new LocaleContext(requestLocale, requestLocale == null, hasLocalePrefix);
    }

    private void initLocale() {
        if (supportedLocales.isEmpty()) {
            return;
        }

        if (supportedLocales.size() == 1) {
            requestLocale = supportedLocales.get(0);
        }

        Optional<String> firstUriPart = getUriParts(0).findFirst();
        if (firstUriPart.isPresent()) {
            Optional<Locale> locale = supportedLocales.stream()
                    .filter(supportedLocale -> supportedLocale.getLanguage().equals(firstUriPart.get()))
                    .findFirst();

            if (locale.isPresent()) {
                hasLocalePrefix = true;
                requestLocale = locale.get();
            }
        }
    }

    public String[] getParams() {
        return getUriParts(hasLocalePrefix ? 2 : 1).toArray(String[]::new);
    }

    public String getFriendlyUrl() {
        return getFriendlyUrl(indexPage.getFriendlyUrl());
    }

    public String getRedirectUrl() {
        return getFriendlyUrl("");
    }

    public String getPathWithoutLocalePrefix() {
        return getUriParts(hasLocalePrefix ? 1 : 0).collect(Collectors.joining("/"));
    }

    private String getFriendlyUrl(String fallback) {
        return getUriParts(hasLocalePrefix ? 1 : 0).findFirst().filter(s -> !s.isEmpty()).orElse(fallback);
    }

    private Stream<String> getUriParts(int skip) {
        return Stream.of(path.split("/")).skip(skip);
    }
}
