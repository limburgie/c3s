package be.webfactor.c3s.master.service;

import java.util.List;

import be.webfactor.c3s.master.domain.Page;
import be.webfactor.c3s.master.domain.Site;
import be.webfactor.c3s.repository.RepositoryConnection;
import be.webfactor.c3s.repository.RepositoryType;

public interface MasterService {

	void initialize(RepositoryConnection connection);

	Site getSite();

	List<Page> getPages();

	Page getPage(String friendlyUrl);

	String getAssetUrl(String assetPath);

	RepositoryType getType();
}
