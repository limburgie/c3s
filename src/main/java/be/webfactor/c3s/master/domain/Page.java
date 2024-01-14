package be.webfactor.c3s.master.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.*;

@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Page {

	@EqualsAndHashCode.Include
	private String friendlyUrl;
	private boolean hidden;
	private String name;
	private String contents;
	private Template template;
	private Map<String, String> inserts;
	private List<Page> children;
	private boolean indexPage;

	public boolean isTemplated() {
		return template != null;
	}
}
