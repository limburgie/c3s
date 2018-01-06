package be.webfactor.c3s.content.service.prismic;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import be.webfactor.c3s.content.service.domain.FieldContainer;
import be.webfactor.c3s.content.service.domain.GroupField;
import io.prismic.Fragment;

public class PrismicGroupField implements GroupField {

	private Fragment.Group group;

	PrismicGroupField(Fragment.Group group) {
		this.group = group;
	}

	public List<FieldContainer> getItems() {
		return group == null ? Collections.emptyList() : group.getDocs().stream().map(PrismicFieldContainer::new).collect(Collectors.toList());
	}
}
