package be.webfactor.c3s.renderer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.content.service.ContentService;
import be.webfactor.c3s.content.service.ContentServiceFactory;
import be.webfactor.c3s.master.domain.Site;
import be.webfactor.c3s.master.service.MasterService;
import be.webfactor.c3s.master.templateparser.TemplateParser;
import be.webfactor.c3s.master.templateparser.TemplateParserFactory;

@Service
public class PageRendererFactory {

	@Autowired private TemplateParserFactory templateParserFactory;
	@Autowired private ContentServiceFactory contentServiceFactory;

	public PageRenderer forMasterService(MasterService masterService) {
		Site site = masterService.getSite();

		TemplateParser templateParser = templateParserFactory.forTemplateEngine(site.getTemplateEngine());
		ContentService contentService = contentServiceFactory.forRepositoryConnection(site.getRepositoryConnection());

		return new PageRenderer(masterService, contentService, templateParser);
	}
}
