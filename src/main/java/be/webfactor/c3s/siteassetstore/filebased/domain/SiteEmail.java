package be.webfactor.c3s.siteassetstore.filebased.domain;

import lombok.Data;

@Data
public class SiteEmail {

	private String subject;
	private String contents;
}
