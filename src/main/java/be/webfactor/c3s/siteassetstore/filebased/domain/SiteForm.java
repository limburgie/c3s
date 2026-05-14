package be.webfactor.c3s.siteassetstore.filebased.domain;

import lombok.Data;

@Data
public class SiteForm {

	private String name;
	private SiteEmail visitorEmail;
	private SiteEmail managerEmail;
}
