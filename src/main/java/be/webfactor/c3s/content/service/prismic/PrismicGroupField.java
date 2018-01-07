package be.webfactor.c3s.content.service.prismic;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import be.webfactor.c3s.content.service.domain.FieldContainer;
import be.webfactor.c3s.content.service.domain.GroupField;
import io.prismic.Api;
import io.prismic.Fragment;

public class PrismicGroupField implements GroupField {

	private Fragment.Group group;
	private Api api;

	PrismicGroupField(Fragment.Group group, Api api) {
		this.group = group;
		this.api = api;
	}

	public List<FieldContainer> getItems() {
		return group == null ? Collections.emptyList() : group.getDocs().stream().map(withFragments -> new PrismicFieldContainer(withFragments, api)).collect(Collectors.toList());
	}
}
