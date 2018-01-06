package be.webfactor.c3s.content.service.domain;

public interface NumberField {

	String format(String pattern);

	String format(String pattern, String locale);
}
