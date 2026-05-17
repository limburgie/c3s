package be.webfactor.c3s.renderer;

import be.webfactor.c3s.contentrepository.ContentRepository;
import be.webfactor.c3s.contentrepository.ContentRepositoryConnection;
import be.webfactor.c3s.contentrepository.ContentRepositoryFactory;
import be.webfactor.c3s.siteassetstore.SiteAssetStore;
import be.webfactor.c3s.templateparser.TemplateParser;
import be.webfactor.c3s.templateparser.TemplateParserFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PageRendererFactory {

	private final TemplateParserFactory templateParserFactory;
	private final ContentRepositoryFactory contentRepositoryFactory;

	public PageRenderer forSiteAssetStore(SiteAssetStore siteAssetStore) {
		TemplateParser templateParser = templateParserFactory.forTemplateEngine(siteAssetStore.getTemplateEngine());

		ContentRepositoryConnection repoConnection = siteAssetStore.getContentRepositoryConnection();
		ContentRepository contentRepository = repoConnection == null ? null : contentRepositoryFactory.forConnection(repoConnection);

		return new PageRenderer(siteAssetStore, templateParser, contentRepository);
	}
}
