package be.webfactor.c3s.controller.sitemap;

import be.webfactor.c3s.master.domain.Page;
import be.webfactor.c3s.master.service.MasterService;
import cz.jiripinkas.jsitemapgenerator.WebPage;
import cz.jiripinkas.jsitemapgenerator.generator.SitemapGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class SitemapBuilder {

	public String generate(HttpServletRequest request, MasterService masterService) throws MalformedURLException {
        return SitemapGenerator.of(getBaseUrl(request)).addPages(createWebPages(masterService)).toString();
	}

	private List<WebPage> createWebPages(MasterService masterService) {
		List<Locale> locales = masterService.getLocales();
		List<Page> pages = masterService.getPages(true);

		List<WebPage> webPages = new ArrayList<>();

		if (locales.isEmpty()) {
			webPages.add(WebPage.builder().nameRoot().build());
			for (Page page : pages) {
				webPages.add(WebPage.builder().name(page.getFriendlyUrl()).build());
			}
		} else {
			for (Locale locale : locales) {
				webPages.add(buildWebPage("", locale, locales));
				for (Page page : pages) {
					webPages.add(buildWebPage(page.getFriendlyUrl(), locale, locales));
				}
			}
		}

		return webPages;
	}

	private WebPage buildWebPage(String friendlyUrl, Locale mainLocale, List<Locale> allLocales) {
        WebPage.WebPageBuilder webPageBuilder = WebPage.builder().name(mainLocale.getLanguage() + "/" + friendlyUrl);
		for (Locale otherLocale : allLocales.stream().filter(l -> !l.equals(mainLocale)).toList()) {
			webPageBuilder.alternateName(otherLocale.getLanguage(), otherLocale.getLanguage() + "/" + friendlyUrl);
		}
		return webPageBuilder.build();
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
