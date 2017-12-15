package be.webfactor.c3s.master.service.prismic;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.master.domain.Page;
import be.webfactor.c3s.master.domain.Site;
import be.webfactor.c3s.master.domain.TemplateEngine;
import be.webfactor.c3s.master.service.MasterService;
import be.webfactor.c3s.repository.RepositoryConnection;
import be.webfactor.c3s.repository.RepositoryType;
import io.prismic.*;

@Service
@Scope("request")
public class PrismicMasterService implements MasterService {

	private Api prismic;

	public void initialize(RepositoryConnection connection) {
		prismic = Api.get(connection.getRepositoryId(), connection.getAccessToken());
	}

	public Site getSite() {
		Response response = prismic.query(Predicates.at("document.type", "site")).submit();
		Document siteDocument = response.getResults().get(0);

		Fragment.DocumentLink pageLink = (Fragment.DocumentLink) siteDocument.getLink("site.index_page");

		String name = siteDocument.getText("site.name");
		String indexPageFriendlyUrl = pageLink.getUid();
		TemplateEngine templateEngine = TemplateEngine.valueOf(siteDocument.getText("site.template_engine"));
		String template = siteDocument.getText("site.template");
		RepositoryType repositoryType = RepositoryType.valueOf(siteDocument.getText("site.content_repository_type"));
		String repositoryId = siteDocument.getText("site.content_repository_id");
		String accessToken = siteDocument.getText("site.content_repository_access_token");

		return new Site(name, getPage(indexPageFriendlyUrl), templateEngine, template, new RepositoryConnection(repositoryType, repositoryId, accessToken));
	}

	public List<Page> getPages() {
		Response response = prismic.query(Predicates.at("document.type", "page")).orderings("my.page.priority").submit();
		return response.getResults().stream().map(pageDocument -> getPage(pageDocument.getUid())).collect(Collectors.toList());
	}

	public Page getPage(String friendlyUrl) {
		Document document = prismic.getByUID("page", friendlyUrl);

		String name = document.getText("page.name");
		String template = document.getText("page.template");

		return new Page(friendlyUrl, name, template);
	}

	public String getAssetUrl(String assetPath) {
		Response response = prismic.query(Predicates.at("document.type", "asset"), Predicates.at("my.asset.path", assetPath)).submit();
		Document assetDocument = response.getResults().get(0);

		return assetDocument.getLink("asset.file").getUrl(null);
	}

	public RepositoryType getType() {
		return RepositoryType.PRISMIC;
	}
}
