package be.webfactor.c3s.content.service.prismic;

import java.time.temporal.TemporalAccessor;

import be.webfactor.c3s.content.service.domain.DateBuilder;
import be.webfactor.c3s.content.service.domain.DateField;
import io.prismic.Fragment;

public class PrismicDateField implements DateField {

	private TemporalAccessor temporalAccessor;

	PrismicDateField(Fragment fragment) {
		if (fragment instanceof Fragment.Date) {
			temporalAccessor = ((Fragment.Date) fragment).getValue();
		} else {
			temporalAccessor = ((Fragment.Timestamp) fragment).getValue();
		}
	}

	public DateBuilder format(String pattern) {
		return new DateBuilder(temporalAccessor, pattern);
	}
}