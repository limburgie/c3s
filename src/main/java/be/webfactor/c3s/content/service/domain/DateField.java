package be.webfactor.c3s.content.service.domain;

public interface DateField extends ValueWrapper {

	String format(String pattern);

	String format(String pattern, String locale);
}
