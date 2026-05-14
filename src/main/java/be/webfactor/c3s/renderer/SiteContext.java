package be.webfactor.c3s.renderer;

import be.webfactor.c3s.siteassetstore.domain.Page;
import lombok.Value;

import java.util.List;
import java.util.Locale;

@Value
public class SiteContext {
    String name;
    List<Page> pages;
    List<Locale> locales;
}
