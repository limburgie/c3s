package be.webfactor.c3s.content.service.mock;

import be.webfactor.c3s.content.service.domain.AssetLink;

public class MockAssetLink implements AssetLink {

	private String type;

	MockAssetLink(String type) {
		this.type = type;
	}

	public String getUrl() {
		return MockRandomGenerator.url();
	}

	public String getFilename() {
		return type + ".pdf";
	}

	public String getExtension() {
		return "pdf";
	}

	public String getType() {
		return "application/pdf";
	}

	public Long getSize() {
		return (long) MockRandomGenerator.integer();
	}

	public boolean isEmpty() {
		return false;
	}
}
