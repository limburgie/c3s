package be.webfactor.c3s.controller.sitemap;

import be.webfactor.c3s.master.domain.Page;
import be.webfactor.c3s.master.service.MasterService;
import cz.jiripinkas.jsitemapgenerator.WebPage;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class SitemapGenerator {

	public String generate(HttpServletRequest request, MasterService masterService) throws MalformedURLException {
		String baseUrl = getBaseUrl(request);
		List<Page> sitemapPages = getSitemapPages(masterService.getPages(true));

		return cz.jiripinkas.jsitemapgenerator.generator.SitemapGenerator.of(baseUrl)
				.addPage(WebPage.builder().maxPriorityRoot().build())
				.addPages(sitemapPages.stream().map(page -> {
                    WebPage.WebPageBuilder builder = WebPage.builder().name(page.getFriendlyUrl());
					if (masterService.getLocales().size() > 1) {
						for (Locale locale : masterService.getLocales()) {
							builder.alternateName(locale.getLanguage(), page.getFriendlyUrl() + "/" + locale.getLanguage());
						}
					}
					return builder.build();
                }).collect(Collectors.toList()))
				.toString();
	}

	private List<Page> getSitemapPages(List<Page> pages) {
		return pages.stream().filter(page -> pages.indexOf(page) == 0 || !page.isHidden()).collect(Collectors.toList());
	}

	private String getBaseUrl(HttpServletRequest request) throws MalformedURLException {
		URL url = new URL(request.getRequestURL().toString());

		String protocol = url.getProtocol();
		String host = url.getHost();
		int port = url.getPort();

		if (port == -1) {
			return String.format("%s://%s", protocol, host);
		} else {
			return String.format("%s://%s:%d", protocol, host, port);
		}
	}
}
