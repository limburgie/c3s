package be.webfactor.c3s.controller.sitemap;

import be.webfactor.c3s.master.domain.Page;
import cz.jiripinkas.jsitemapgenerator.ChangeFreq;
import cz.jiripinkas.jsitemapgenerator.WebPage;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SitemapGenerator {

	public String generate(HttpServletRequest request, List<Page> pages) throws MalformedURLException {
		String baseUrl = getBaseUrl(request);

		return cz.jiripinkas.jsitemapgenerator.generator.SitemapGenerator.of(baseUrl)
				.addPages(pages.stream().map(page -> WebPage.builder()
						.name(page.getFriendlyUrl())
						.changeFreq(ChangeFreq.MONTHLY)
						.priority(getPagePriority(pages, page))
						.build()).collect(Collectors.toList()))
				.toString();
	}

	private Double getPagePriority(List<Page> pages, Page page) {
		if (pages.indexOf(page) == 0) {
			return 1.0;
		}

		int position = pages.indexOf(page);

		return 1.0 - (double) position / pages.size();
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
