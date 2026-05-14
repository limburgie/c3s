package be.webfactor.c3s.master.service.filebased.domain;

import lombok.Data;

@Data
public class SiteEmail {

	private String subject;
	private String contents;
}
