package be.webfactor.c3s.content.service.graphcms;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import be.webfactor.c3s.content.service.domain.DateBuilder;
import be.webfactor.c3s.content.service.domain.DateField;

public class GraphCmsDateField implements DateField {

	private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

	private TemporalAccessor temporalAccessor;

	GraphCmsDateField(String stringRepresentationOfDate) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);

		temporalAccessor = dateFormatter.parse(stringRepresentationOfDate);
	}

	public DateBuilder format(String pattern) {
		return new DateBuilder(temporalAccessor, pattern);
	}
}
