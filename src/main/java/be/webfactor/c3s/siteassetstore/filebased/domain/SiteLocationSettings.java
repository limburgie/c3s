package be.webfactor.c3s.siteassetstore.filebased.domain;

import lombok.Data;

import java.util.List;

@Data
public class SiteLocationSettings {

	private List<String> locales;
	private String timeZone;

	public String getDefaultLocale() {
		return (locales == null || locales.isEmpty()) ? null : locales.get(0);
	}
}
