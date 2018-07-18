package be.webfactor.c3s.content.service.mock;

import java.time.ZonedDateTime;
import java.util.List;

import be.webfactor.c3s.content.service.domain.*;

public class MockContentItem extends MockFieldContainer implements ContentItem {

	private String type;

	MockContentItem(String type) {
		super(type);

		this.type = type;
	}

	public List<FieldContainer> getGroup(String fieldName) {
		return MockRandomGenerator.groupItemList(type, MockRandomGenerator.smallInt());
	}

	public String getUid() {
		return getText("uid");
	}

	public String getId() {
		return getText("id");
	}

	public DateBuilder getCreated() {
		return new DateBuilder(ZonedDateTime.now());
	}

	public DateBuilder getModified() {
		return new DateBuilder(ZonedDateTime.now());
	}
}
