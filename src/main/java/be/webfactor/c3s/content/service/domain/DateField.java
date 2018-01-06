package be.webfactor.c3s.content.service.domain;

public interface DateField {

	String format(String pattern);

	String format(String pattern, String locale);
}
