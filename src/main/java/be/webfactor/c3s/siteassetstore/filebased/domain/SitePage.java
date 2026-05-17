package be.webfactor.c3s.siteassetstore.filebased.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SitePage {

	private String friendlyUrl;
	private String name;
	private boolean hidden;
	private String contents;
	private String template;
	private Map<String, String> inserts = new HashMap<>();
	private List<SitePage> children = new ArrayList<>();

	public boolean isTemplated() {
		return template != null;
	}
}
