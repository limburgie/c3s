package be.webfactor.c3s.master.service.prismic;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.print.Doc;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.master.domain.Page;
import be.webfactor.c3s.master.domain.Template;
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
		Fragment.DocumentLink templateLink = (Fragment.DocumentLink) document.getLink("page.template");
		Map<String, String> defines = getDefines(document.getGroup("page.defines"));

		return new Page(friendlyUrl, name, getTemplate(templateLink), defines, Collections.emptyList());
	}

	public Page getIndexPage() {
		return getPage(((Fragment.DocumentLink) (document.getLink("site.index_page"))).getUid());
	}

	public Page getErrorPage() {
		String name = document.getText("site.error_page_name");
		Fragment.DocumentLink templateLink = (Fragment.DocumentLink) document.getLink("site.error_page_template");
		Map<String, String> defines = getDefines(document.getGroup("site_error_page_defines"));

		return new Page(name, getTemplate(templateLink), defines);
	}

	private Template getTemplate(Fragment.DocumentLink documentLink) {
		Document document = prismic.getByID(documentLink.getId());

		String name = document.getText("template.name");
		Fragment.DocumentLink extendedTemplateDoc = ((Fragment.DocumentLink) document.getLink("template.extended_template"));

		if (extendedTemplateDoc == null) {
			String contents = document.getText("template.contents");

			return new Template(name, contents);
		} else {
			Template extendedTemplate = getTemplate(extendedTemplateDoc);
			Map<String, String> defines = getDefines(document.getGroup("template.defines"));

			return new Template(name, extendedTemplate, defines);
		}
	}

	private Map<String, String> getDefines(Fragment.Group documentGroup) {
		List<GroupDoc> contextParams = documentGroup.getDocs();
		Map<String, String> result = new HashMap<>();

		for (GroupDoc contextParam : contextParams) {
			String key = contextParam.getText("key");
			String value = contextParam.getText("value");

			result.put(key, value);
		}

		return result;
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
