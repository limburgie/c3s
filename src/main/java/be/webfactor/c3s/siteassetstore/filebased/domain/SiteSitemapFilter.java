package be.webfactor.c3s.siteassetstore.filebased.domain;

import lombok.Data;

@Data
public class SiteSitemapFilter {

	private String type;
	private String field;
	private String value;
}
