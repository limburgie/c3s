package be.webfactor.c3s.renderer.cache;

import be.webfactor.c3s.siteassetstore.domain.LocaleContext;

/**
 * Identifies a rendered page: the site (its asset store repository id), the locale context (locale and whether the
 * URI is locale prefixed, which both influence the rendered links) and the path without locale prefix, which holds
 * the friendly URL followed by the path params. The query string is not part of the key as templates cannot read it.
 */
public record PageCacheKey(String site, LocaleContext localeContext, String path) {
}
