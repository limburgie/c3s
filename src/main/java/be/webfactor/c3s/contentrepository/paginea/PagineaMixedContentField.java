package be.webfactor.c3s.contentrepository.paginea;

import be.webfactor.c3s.contentrepository.domain.MixedContentField;
import be.webfactor.c3s.contentrepository.domain.MixedContentItem;
import be.webfactor.c3s.contentrepository.paginea.model.PagineaMixedContentFieldDto;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PagineaMixedContentField implements MixedContentField {

    private final PagineaMixedContentFieldDto mixedContentFieldDto;

    @Override
    public List<MixedContentItem> getItems() {
        return mixedContentFieldDto.getItems().stream()
                .map(PagineaMixedContentItem::new)
                .collect(Collectors.toList());
    }
}
