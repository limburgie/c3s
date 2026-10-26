package be.webfactor.c3s.controller.sitemap;

import be.webfactor.c3s.contentrepository.ContentRepository;
import be.webfactor.c3s.contentrepository.ContentRepositoryConnection;
import be.webfactor.c3s.contentrepository.ContentRepositoryFactory;
import be.webfactor.c3s.contentrepository.ContentRepositoryType;
import be.webfactor.c3s.contentrepository.domain.ContentApi;
import be.webfactor.c3s.contentrepository.domain.ContentItem;
import be.webfactor.c3s.contentrepository.domain.DateBuilder;
import be.webfactor.c3s.contentrepository.domain.QueryBuilder;
import be.webfactor.c3s.siteassetstore.SiteAssetStore;
import be.webfactor.c3s.siteassetstore.domain.*;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Verifies that the sitemap lists every visible page, including nested child pages, and lists the index page only
 * once (at the root URL), even when the index page is not the first page in the site configuration. Pages with
 * sitemap settings also list their content-driven detail pages, per locale in which the content exists.
 */
class SitemapBuilderTest {

	private static final ContentRepositoryConnection CONNECTION = ContentRepositoryConnection.of(ContentRepositoryType.MOCK, "repo", "token");
	private static final Locale NL = Locale.of("nl");
	private static final Locale FR = Locale.of("fr");

	private final ContentRepositoryFactory contentRepositoryFactory = mock(ContentRepositoryFactory.class);
	private final SitemapBuilder sitemapBuilder = new SitemapBuilder(contentRepositoryFactory);

	/**
	 * Values of the param field returned by the content repository, per language of the current locale context.
	 */
	private final Map<String, List<String>> slugsByLanguage = new HashMap<>();
	/**
	 * Last modified dates returned by the content repository, per language and slug. Items without one have no date.
	 */
	private final Map<String, Map<String, TemporalAccessor>> modifiedByLanguage = new HashMap<>();
	private final List<QueryBuilder> queryBuilders = new ArrayList<>();

	@Test
	void includesNestedChildPages() throws Exception {
		Page about = page("about", page("team", page("history")));
		SiteAssetStore store = store(List.of(page("home"), about), page("home"), List.of());

		Set<String> locs = locs(sitemapBuilder.generate(request(), store).xml());

		assertEquals(Set.of("https://www.example.com/", "https://www.example.com/about",
				"https://www.example.com/team", "https://www.example.com/history"), locs);
	}

	@Test
	void excludesConfiguredIndexPageInsteadOfFirstPage() throws Exception {
		SiteAssetStore store = store(List.of(page("intro"), page("home"), page("contact")), page("home"), List.of());

		Set<String> locs = locs(sitemapBuilder.generate(request(), store).xml());

		assertEquals(Set.of("https://www.example.com/", "https://www.example.com/intro",
				"https://www.example.com/contact"), locs);
	}

	@Test
	void prefixesEveryPageWithEachLocale() throws Exception {
		SiteAssetStore store = store(List.of(page("home"), page("contact", page("route"))), page("home"),
				List.of(Locale.of("nl"), Locale.of("fr")));

		Set<String> locs = locs(sitemapBuilder.generate(request(), store).xml());

		assertEquals(Set.of("https://www.example.com/fr/", "https://www.example.com/fr/contact",
				"https://www.example.com/fr/route", "https://www.example.com/nl/", "https://www.example.com/nl/contact",
				"https://www.example.com/nl/route"), locs);
	}

	@Test
	void sitemapUrlUsesRequestHost() throws Exception {
		assertEquals("https://www.example.com/sitemap.xml", sitemapBuilder.getSitemapUrl(request()));
	}

	@Test
	void listsDetailPagesPerLocale() throws Exception {
		slugsByLanguage.put("nl", List.of("eerste", "tweede"));
		slugsByLanguage.put("fr", List.of("eerste", "tweede"));
		SiteAssetStore store = storeWithConnection(List.of(page("home"), blog()), page("home"), List.of(NL, FR));

		Set<String> locs = locs(sitemapBuilder.generate(request(), store).xml());

		assertEquals(Set.of("https://www.example.com/nl/", "https://www.example.com/nl/blog",
				"https://www.example.com/nl/blog/eerste", "https://www.example.com/nl/blog/tweede",
				"https://www.example.com/fr/", "https://www.example.com/fr/blog",
				"https://www.example.com/fr/blog/eerste", "https://www.example.com/fr/blog/tweede"), locs);
	}

	@Test
	void omitsUrlAndAlternateForLocaleWithoutItem() throws Exception {
		slugsByLanguage.put("nl", List.of("eerste", "tweede"));
		slugsByLanguage.put("fr", List.of("eerste"));
		SiteAssetStore store = storeWithConnection(List.of(page("home"), blog()), page("home"), List.of(NL, FR));

		String sitemapXml = sitemapBuilder.generate(request(), store).xml();

		assertTrue(locs(sitemapXml).contains("https://www.example.com/nl/blog/tweede"));
		assertFalse(locs(sitemapXml).contains("https://www.example.com/fr/blog/tweede"));
		assertEquals(Set.of("nl", "x-default"), alternates(sitemapXml).get("https://www.example.com/nl/blog/tweede"));
		assertEquals(Set.of("nl", "fr", "x-default"), alternates(sitemapXml).get("https://www.example.com/nl/blog/eerste"));
		assertEquals(Set.of("nl", "fr", "x-default"), alternates(sitemapXml).get("https://www.example.com/nl/blog"));
	}

	@Test
	void appliesFiltersInOrder() throws Exception {
		slugsByLanguage.put("nl", List.of("eerste"));
		Page blog = page("blog", new SitemapSettings("blog", "slug", List.of(
				new SitemapFilter("tag", null, "news"),
				new SitemapFilter("equals", "category", "food"),
				new SitemapFilter("dateInPast", "date", null))));
		SiteAssetStore store = storeWithConnection(List.of(page("home"), blog), page("home"), List.of(NL));

		sitemapBuilder.generate(request(), store).xml();

		QueryBuilder queryBuilder = queryBuilders.getFirst();
		InOrder inOrder = inOrder(queryBuilder);
		inOrder.verify(queryBuilder).withTag("news");
		inOrder.verify(queryBuilder).with("category", "food");
		inOrder.verify(queryBuilder).withDateInPast("date");
		inOrder.verify(queryBuilder).findAll();
	}

	@Test
	void skipsBlankValuesAndEncodesValues() throws Exception {
		slugsByLanguage.put("nl", Arrays.asList("café au lait", " ", null, "a/b"));
		SiteAssetStore store = storeWithConnection(List.of(page("home"), blog()), page("home"), List.of(NL));

		Set<String> locs = locs(sitemapBuilder.generate(request(), store).xml());

		assertEquals(Set.of("https://www.example.com/nl/", "https://www.example.com/nl/blog",
				"https://www.example.com/nl/blog/caf%C3%A9%20au%20lait", "https://www.example.com/nl/blog/a%2Fb"), locs);
	}

	@Test
	void listsOnlyStaticPagesWithoutContentRepository() throws Exception {
		SiteAssetStore store = store(List.of(page("home"), blog()), page("home"), List.of(NL));

		Sitemap sitemap = sitemapBuilder.generate(request(), store);

		assertEquals(Set.of("https://www.example.com/nl/", "https://www.example.com/nl/blog"), locs(sitemap.xml()));
		assertTrue(sitemap.complete());
		verifyNoInteractions(contentRepositoryFactory);
	}

	@Test
	void listsStaticPagesWhenQueryFails() throws Exception {
		SiteAssetStore store = storeWithConnection(List.of(page("home"), blog()), page("home"), List.of(NL));
		ContentApi api = mock(ContentApi.class);
		when(api.query("blog")).thenThrow(new IllegalStateException("CMS down"));
		ContentRepository repository = mock(ContentRepository.class);
		when(repository.getApi()).thenReturn(api);
		when(contentRepositoryFactory.forConnection(CONNECTION)).thenReturn(repository);

		Sitemap sitemap = sitemapBuilder.generate(request(), store);

		assertEquals(Set.of("https://www.example.com/nl/", "https://www.example.com/nl/blog"), locs(sitemap.xml()));
		assertFalse(sitemap.complete());
	}

	@Test
	void isIncompleteWhenContentRepositoryUnavailable() throws Exception {
		SiteAssetStore store = storeWithConnection(List.of(page("home"), blog()), page("home"), List.of(NL, FR));
		when(contentRepositoryFactory.forConnection(CONNECTION)).thenThrow(new IllegalStateException("CMS down"));

		Sitemap sitemap = sitemapBuilder.generate(request(), store);

		assertTrue(locs(sitemap.xml()).contains("https://www.example.com/fr/blog"));
		assertFalse(sitemap.complete());
	}

	@Test
	void isCompleteWhenAllQueriesSucceed() throws Exception {
		slugsByLanguage.put("nl", List.of("eerste"));
		SiteAssetStore store = storeWithConnection(List.of(page("home"), blog()), page("home"), List.of(NL, FR));

		assertTrue(sitemapBuilder.generate(request(), store).complete());
	}

	@Test
	void skipsDetailPagesWithInvalidFilter() throws Exception {
		slugsByLanguage.put("nl", List.of("eerste"));
		Page blog = page("blog", new SitemapSettings("blog", "slug", List.of(new SitemapFilter("unknown", "date", null))));
		SiteAssetStore store = storeWithConnection(List.of(page("home"), blog), page("home"), List.of(NL));

		Sitemap sitemap = sitemapBuilder.generate(request(), store);

		// A configuration error is not fixed by retrying, so the sitemap may be cached until the next deploy
		assertEquals(Set.of("https://www.example.com/nl/", "https://www.example.com/nl/blog"), locs(sitemap.xml()));
		assertTrue(sitemap.complete());
	}

	@Test
	void listsDetailPagesWithoutLanguagePrefixOnNonLocalizedSite() throws Exception {
		slugsByLanguage.put(LocaleContext.DEFAULT_LOCALE.getLanguage(), List.of("eerste"));
		SiteAssetStore store = storeWithConnection(List.of(page("home"), blog()), page("home"), List.of());

		Set<String> locs = locs(sitemapBuilder.generate(request(), store).xml());

		assertEquals(Set.of("https://www.example.com/", "https://www.example.com/blog",
				"https://www.example.com/blog/eerste"), locs);
	}

	@Test
	void setsLastModOfDetailPagesPerLocale() throws Exception {
		slugsByLanguage.put("nl", List.of("eerste", "tweede"));
		slugsByLanguage.put("fr", List.of("eerste"));
		modifiedByLanguage.put("nl", Map.of("eerste", ZonedDateTime.parse("2026-03-01T10:15:30Z")));
		modifiedByLanguage.put("fr", Map.of("eerste", OffsetDateTime.parse("2026-04-02T08:00:00+02:00")));
		SiteAssetStore store = storeWithConnection(List.of(page("home"), blog()), page("home"), List.of(NL, FR));

		Map<String, Instant> lastMods = lastMods(sitemapBuilder.generate(request(), store).xml());

		assertEquals(Instant.parse("2026-03-01T10:15:30Z"), lastMods.get("https://www.example.com/nl/blog/eerste"));
		assertEquals(Instant.parse("2026-04-02T06:00:00Z"), lastMods.get("https://www.example.com/fr/blog/eerste"));
		assertFalse(lastMods.containsKey("https://www.example.com/nl/blog/tweede"));
		assertFalse(lastMods.containsKey("https://www.example.com/nl/blog"));
	}

	@Test
	void interpretsLastModWithoutOffsetInSiteTimeZone() throws Exception {
		LocationThreadLocal.setTimeZone(ZoneId.of("Europe/Brussels"));
		try {
			slugsByLanguage.put(LocaleContext.DEFAULT_LOCALE.getLanguage(), List.of("eerste"));
			modifiedByLanguage.put(LocaleContext.DEFAULT_LOCALE.getLanguage(), Map.of("eerste", LocalDateTime.parse("2026-07-01T12:00:00")));
			SiteAssetStore store = storeWithConnection(List.of(page("home"), blog()), page("home"), List.of());

			Map<String, Instant> lastMods = lastMods(sitemapBuilder.generate(request(), store).xml());

			assertEquals(Instant.parse("2026-07-01T10:00:00Z"), lastMods.get("https://www.example.com/blog/eerste"));
		} finally {
			LocationThreadLocal.setTimeZone(null);
		}
	}

	@Test
	void keepsDetailPageWhenLastModCannotBeRead() throws Exception {
		slugsByLanguage.put("nl", List.of("eerste"));
		SiteAssetStore store = storeWithConnection(List.of(page("home"), blog()), page("home"), List.of(NL));
		ContentRepository repository = repository(List.of("eerste"), Map.of());
		ContentItem item = repository.getApi().query("blog").findAll().getFirst();
		when(item.getModified(any())).thenThrow(new IllegalArgumentException("Unparsable date"));
		when(contentRepositoryFactory.forConnection(CONNECTION)).thenReturn(repository);

		String sitemapXml = sitemapBuilder.generate(request(), store).xml();

		assertTrue(locs(sitemapXml).contains("https://www.example.com/nl/blog/eerste"));
		assertTrue(lastMods(sitemapXml).isEmpty());
	}

	@Test
	void restoresLocaleContext() throws Exception {
		LocaleContext original = new LocaleContext();
		LocationThreadLocal.setLocaleContext(original);
		slugsByLanguage.put("nl", List.of("eerste"));
		SiteAssetStore store = storeWithConnection(List.of(page("home"), blog()), page("home"), List.of(NL));

		sitemapBuilder.generate(request(), store).xml();

		assertSame(original, LocationThreadLocal.getLocaleContext());
	}

	private static Page blog() {
		return page("blog", new SitemapSettings("blog", "slug", List.of()));
	}

	private static Page page(String friendlyUrl, SitemapSettings sitemap) {
		return Page.builder().friendlyUrl(friendlyUrl).name(friendlyUrl).children(List.of()).sitemap(sitemap).build();
	}

	/**
	 * Returns a store whose content repository answers with the slugs for the language of the locale context that is
	 * current when the repository is initialized, like backends that fix the language when their API is created.
	 */
	private SiteAssetStore storeWithConnection(List<Page> pages, Page indexPage, List<Locale> locales) {
		SiteAssetStore store = store(pages, indexPage, locales);
		when(store.getContentRepositoryConnection()).thenReturn(CONNECTION);
		when(contentRepositoryFactory.forConnection(CONNECTION)).thenAnswer(invocation -> {
			String language = LocationThreadLocal.getLocaleContext().locale().getLanguage();
			return repository(slugsByLanguage.getOrDefault(language, List.of()), modifiedByLanguage.getOrDefault(language, Map.of()));
		});
		return store;
	}

	private ContentRepository repository(List<String> slugs, Map<String, TemporalAccessor> modified) {
		List<ContentItem> items = slugs.stream().map(slug -> {
			ContentItem item = mock(ContentItem.class);
			when(item.getText("slug")).thenReturn(slug);
			when(item.getModified(any())).thenAnswer(invocation -> new DateBuilder(modified.get(slug), invocation.getArgument(0)));
			return item;
		}).toList();

		ContentApi api = mock(ContentApi.class);
		when(api.query(any())).thenAnswer(invocation -> {
			QueryBuilder queryBuilder = mock(QueryBuilder.class, RETURNS_SELF);
			doReturn(items).when(queryBuilder).findAll();
			queryBuilders.add(queryBuilder);
			return queryBuilder;
		});

		ContentRepository repository = mock(ContentRepository.class);
		when(repository.getApi()).thenReturn(api);
		return repository;
	}

	private static Page page(String friendlyUrl, Page... children) {
		return Page.builder().friendlyUrl(friendlyUrl).name(friendlyUrl).children(List.of(children)).build();
	}

	private static SiteAssetStore store(List<Page> pages, Page indexPage, List<Locale> locales) {
		SiteAssetStore store = mock(SiteAssetStore.class);
		when(store.getPages(false)).thenReturn(pages);
		when(store.getIndexPage()).thenReturn(indexPage);
		when(store.getLocales()).thenReturn(locales);
		return store;
	}

	private static MockHttpServletRequest request() {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/sitemap.xml");
		request.setScheme("https");
		request.setServerName("www.example.com");
		request.setServerPort(443);
		return request;
	}

	/**
	 * The generator sorts URLs alphabetically, so only the set of URLs is meaningful. Duplicates fail the test.
	 */
	private static Set<String> locs(String sitemapXml) {
		Matcher matcher = Pattern.compile("<loc>([^<]*)</loc>").matcher(sitemapXml);
		List<String> locs = matcher.results().map(result -> result.group(1)).toList();
		assertEquals(locs.size(), Set.copyOf(locs).size(), "Duplicate URLs in sitemap: " + locs);
		return Set.copyOf(locs);
	}

	/**
	 * Returns the lastmod per URL, for URLs that have one.
	 */
	private static Map<String, Instant> lastMods(String sitemapXml) {
		Matcher urlMatcher = Pattern.compile("<url>(.*?)</url>", Pattern.DOTALL).matcher(sitemapXml);
		return urlMatcher.results().map(result -> result.group(1)).filter(url -> url.contains("<lastmod>")).collect(Collectors.toMap(
				url -> url.replaceAll("(?s).*<loc>([^<]*)</loc>.*", "$1"),
				url -> OffsetDateTime.parse(url.replaceAll("(?s).*<lastmod>([^<]*)</lastmod>.*", "$1")).toInstant()));
	}

	/**
	 * Returns the hreflang values of the alternates per URL.
	 */
	private static Map<String, Set<String>> alternates(String sitemapXml) {
		Matcher urlMatcher = Pattern.compile("<url>(.*?)</url>", Pattern.DOTALL).matcher(sitemapXml);
		return urlMatcher.results().map(result -> result.group(1)).collect(Collectors.toMap(
				url -> url.replaceAll("(?s).*<loc>([^<]*)</loc>.*", "$1"),
				url -> Pattern.compile("hreflang=\"([^\"]*)\"").matcher(url).results()
						.map(hreflang -> hreflang.group(1)).collect(Collectors.toSet())));
	}
}
