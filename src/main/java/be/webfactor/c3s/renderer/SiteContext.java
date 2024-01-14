package be.webfactor.c3s.renderer;

import java.util.List;
import java.util.Locale;

import be.webfactor.c3s.master.domain.Page;
import lombok.Value;

@Value
public class SiteContext {

	String name;
	List<Page> pages;
	List<Locale> locales;
}
