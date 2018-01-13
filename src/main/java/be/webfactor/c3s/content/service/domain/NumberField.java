package be.webfactor.c3s.content.service.domain;

public interface NumberField extends ValueWrapper {

	String format(String pattern);

	String format(String pattern, String locale);
}
