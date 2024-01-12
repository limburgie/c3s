package be.webfactor.c3s.renderer;

import java.util.List;

import be.webfactor.c3s.master.domain.Page;
import lombok.Value;

@Value
public class SiteContext {

	String name;
	List<Page> pages;
}
