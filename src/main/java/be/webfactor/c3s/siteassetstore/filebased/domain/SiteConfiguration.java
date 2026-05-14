package be.webfactor.c3s.siteassetstore.filebased.domain;

import lombok.Data;
import org.apache.commons.lang3.LocaleUtils;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class SiteConfiguration {

	private String name;
	private String indexPage;
	private String templateEngine;
	private SiteContentRepositoryConnection contentRepositoryConnection;
	private SiteLocationSettings locationSettings;
	private SiteMailSettings mailSettings;
	private SitePage errorPage;
	private List<SiteTemplate> templates;
	private List<SitePage> pages;
	private List<SiteForm> forms;

	public List<SitePage> getAllPages() {
		return getDescendantsOf(pages);
	}

	private List<SitePage> getDescendantsOf(List<SitePage> pages) {
		List<SitePage> results = new ArrayList<>();

		for (SitePage page : pages) {
			results.add(page);
			results.addAll(getDescendantsOf(page.getChildren()));
		}

		return results;
	}

	public List<Locale> getLocales() {
		if (locationSettings == null || locationSettings.getLocales() == null) {
			return Collections.emptyList();
		}

		return locationSettings.getLocales().stream()
				.map(LocaleUtils::toLocale)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}
}
