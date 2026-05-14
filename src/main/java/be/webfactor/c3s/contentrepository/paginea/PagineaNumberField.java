package be.webfactor.c3s.contentrepository.paginea;

import be.webfactor.c3s.contentrepository.domain.NumberBuilder;
import be.webfactor.c3s.contentrepository.domain.NumberField;
import be.webfactor.c3s.contentrepository.paginea.model.PagineaNumberFieldDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PagineaNumberField implements NumberField {

    private final PagineaNumberFieldDto numberField;

    @Override
    public NumberBuilder format(String pattern) {
        return new NumberBuilder(numberField.getNumberValue(), pattern);
    }
}
