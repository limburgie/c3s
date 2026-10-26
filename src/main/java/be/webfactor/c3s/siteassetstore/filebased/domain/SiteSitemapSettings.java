package be.webfactor.c3s.siteassetstore.filebased.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SiteSitemapSettings {

	private String contentType;
	private String paramField;
	private List<SiteSitemapFilter> filters = new ArrayList<>();
}
