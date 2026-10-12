package be.webfactor.c3s.controller;

import be.webfactor.c3s.contentrepository.ContentRepositoryFactory;
import be.webfactor.c3s.controller.helper.site.SiteAssetStoreResolver;
import be.webfactor.c3s.renderer.PageRenderer;
import be.webfactor.c3s.renderer.PageRendererFactory;
import be.webfactor.c3s.renderer.cache.CaffeinePageCache;
import be.webfactor.c3s.shopping.ShoppingCart;
import be.webfactor.c3s.shopping.ShoppingCartService;
import be.webfactor.c3s.shopping.ShoppingCartThreadLocal;
import be.webfactor.c3s.siteassetstore.SiteAssetStore;
import be.webfactor.c3s.siteassetstore.domain.Page;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Verifies that page renders are served from the page cache, keyed on the path params, except for visitors with a
 * non-empty shopping cart, and that failed renders are not cached.
 */
class PageControllerCacheTest {

	private final Page home = Page.builder().friendlyUrl("home").name("home").build();
	private final Page news = Page.builder().friendlyUrl("news").name("news").build();
	private final Page errorPage = Page.builder().name("error").build();

	private final SiteAssetStore store = mock(SiteAssetStore.class);
	private final PageRenderer renderer = mock(PageRenderer.class);

	private PageController controller;

	@BeforeEach
	void setUp() {
		when(store.getBaseUrl()).thenReturn("site");
		when(store.getLocales()).thenReturn(List.of(Locale.of("nl")));
		when(store.getIndexPage()).thenReturn(home);
		when(store.getPage("home")).thenReturn(home);
		when(store.getPage("news")).thenReturn(news);
		when(store.getErrorPage()).thenReturn(errorPage);
		when(renderer.render(eq(home), any())).thenReturn("home");
		when(renderer.render(eq(news), any())).thenReturn("news");
		when(renderer.render(eq(errorPage), any())).thenReturn("error");

		SiteAssetStoreResolver resolver = mock(SiteAssetStoreResolver.class);
		when(resolver.forRequest(any(HttpServletRequest.class))).thenReturn(store);
		PageRendererFactory rendererFactory = mock(PageRendererFactory.class);
		when(rendererFactory.forSiteAssetStore(store)).thenReturn(renderer);

		CaffeinePageCache pageCache = new CaffeinePageCache(new SimpleMeterRegistry());
		ReflectionTestUtils.setField(pageCache, "enabled", true);
		ReflectionTestUtils.setField(pageCache, "maxEntries", 1000L);
		ReflectionTestUtils.invokeMethod(pageCache, "init");

		controller = new PageController(resolver, rendererFactory, mock(ContentRepositoryFactory.class), mock(ShoppingCartService.class), pageCache);
		setCart(true);
	}

	@AfterEach
	void tearDown() {
		ShoppingCartThreadLocal.setShoppingCart(null);
	}

	@Test
	void servesRepeatedRequestsFromCache() {
		assertEquals("news", get("/news/article-1").body);
		assertEquals("news", get("/news/article-1").body);

		verify(renderer, times(1)).render(eq(news), any());
	}

	@Test
	void cachesPerPathParams() {
		get("/news/article-1");
		get("/news/article-2");

		verify(renderer, times(2)).render(eq(news), any());
	}

	@Test
	void bypassesCacheForNonEmptyShoppingCart() {
		get("/news/article-1");
		setCart(false);
		get("/news/article-1");
		get("/news/article-1");

		verify(renderer, times(3)).render(eq(news), any());
	}

	@Test
	void doesNotCacheFailedRenders() {
		when(renderer.render(eq(news), any())).thenThrow(new IllegalStateException()).thenReturn("news");

		Result failed = get("/news/article-1");
		Result succeeded = get("/news/article-1");

		assertEquals(500, failed.status);
		assertEquals("error", failed.body);
		assertEquals(200, succeeded.status);
		assertEquals("news", succeeded.body);
	}

	private void setCart(boolean empty) {
		ShoppingCart cart = mock(ShoppingCart.class);
		when(cart.isEmpty()).thenReturn(empty);
		ShoppingCartThreadLocal.setShoppingCart(cart);
	}

	private Result get(String path) {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
		request.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, path);
		MockHttpServletResponse response = new MockHttpServletResponse();
		String body = controller.friendlyUrl(request, response, null);
		return new Result(response.getStatus(), body);
	}

	private record Result(int status, String body) {}
}
