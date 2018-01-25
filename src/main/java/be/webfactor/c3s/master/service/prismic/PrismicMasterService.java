package be.webfactor.c3s.master.service.prismic;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.master.domain.Page;
import be.webfactor.c3s.master.domain.TemplateEngine;
import be.webfactor.c3s.master.service.MasterService;
import be.webfactor.c3s.repository.RepositoryConnection;
import be.webfactor.c3s.repository.RepositoryType;
import io.prismic.*;

@Service
@Scope("request")
public class PrismicMasterService implements MasterService {

	private Api prismic;
	private Document document;
	private static final String ERROR_PAGE_NAME = "Error";

	public void initialize(RepositoryConnection connection) {
		prismic = Api.get(connection.getRepositoryId(), connection.getAccessToken());
		document = prismic.query(Predicates.at("document.type", "site")).submit().getResults().get(0);
	}

	public List<Page> getPages() {
		Response response = prismic.query(Predicates.at("document.type", "page")).orderings("my.page.priority").submit();
		return response.getResults().stream().map(pageDocument -> getPage(pageDocument.getUid())).collect(Collectors.toList());
	}

	public TemplateEngine getTemplateEngine() {
		return TemplateEngine.valueOf(document.getText("site.template_engine"));
	}

	public RepositoryConnection getRepositoryConnection() {
		RepositoryType type = RepositoryType.valueOf(document.getText("site.content_repository_type"));
		String id = document.getText("site.content_repository_id");
		String accessToken = document.getText("site.content_repository_access_token");

		return new RepositoryConnection(type, id, accessToken);
	}

	public Page getPage(String friendlyUrl) {
		Document document = prismic.getByUID("page", friendlyUrl);

		String name = document.getText("page.name");
		String template = document.getText("page.template");

		return new Page(friendlyUrl, name, template);
	}

	public Page getIndexPage() {
		return getPage(((Fragment.DocumentLink) (document.getLink("site.index_page"))).getUid());
	}

	public Page getErrorPage() {
		return new Page(ERROR_PAGE_NAME, document.getText("site.error_template"));
	}

	public String getAssetUrl(String assetPath) {
		Response response = prismic.query(Predicates.at("document.type", "asset"), Predicates.at("my.asset.path", assetPath)).submit();
		Document assetDocument = response.getResults().get(0);

		return assetDocument.getLink("asset.file").getUrl(null);
	}

	public RepositoryType getType() {
		return RepositoryType.PRISMIC;
	}

	public String getSiteTemplate() {
		return document.getText("site.template");
	}
}
