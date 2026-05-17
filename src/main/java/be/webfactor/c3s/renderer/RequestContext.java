package be.webfactor.c3s.renderer;

import be.webfactor.c3s.siteassetstore.domain.Page;
import lombok.Value;

import java.util.Locale;

@Value
public class RequestContext {
    Page page;
    String[] params;
    Locale locale;
}
