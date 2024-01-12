package be.webfactor.c3s.renderer;

import java.util.Locale;

import be.webfactor.c3s.master.domain.Page;
import lombok.Value;

@Value
public class RequestContext {

	Page page;
	String[] params;
	Locale locale;
}
