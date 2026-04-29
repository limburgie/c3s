package be.webfactor.c3s.content.service.paginea;

import be.webfactor.c3s.content.service.domain.DateBuilder;
import be.webfactor.c3s.content.service.domain.DateField;
import be.webfactor.c3s.content.service.paginea.model.PagineaDateFieldDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PagineaDateField implements DateField {

    private final PagineaDateFieldDto dateField;

    @Override
    public DateBuilder format(String pattern) {
        return new DateBuilder(dateField.getDateValue(), pattern);
    }
}
