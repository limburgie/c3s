package be.webfactor.c3s.controller.webhook;

import be.webfactor.c3s.renderer.cache.PageCache;
import be.webfactor.c3s.siteassetstore.SiteAssetStoreConnection;
import be.webfactor.c3s.siteassetstore.SiteAssetStoreType;
import be.webfactor.c3s.siteassetstore.cache.AssetCache;
import be.webfactor.c3s.siteconnectionregistry.SiteConnectionRegistry;
import be.webfactor.c3s.siteconnectionregistry.SiteConnectionRegistryFactory;
import be.webfactor.c3s.siteconnectionregistry.domain.SiteConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies that each purge webhook only accepts its own secret, that an asset purge also purges the site's pages and
 * that a page purge leaves the site's assets intact.
 */
class CachePurgeControllerTest {

	private static final String HOST = "www.example.com";
	private static final String REPOSITORY_ID = "s3://bucket/example";

	private final AssetCache assetCache = mock(AssetCache.class);
	private final PageCache pageCache = mock(PageCache.class);

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		SiteConnectionRegistry registry = mock(SiteConnectionRegistry.class);
		when(registry.getConnectionForHost(HOST)).thenReturn(new SiteConnection(HOST, "Example", SiteAssetStoreConnection.of(SiteAssetStoreType.S3, REPOSITORY_ID)));
		SiteConnectionRegistryFactory registryFactory = mock(SiteConnectionRegistryFactory.class);
		when(registryFactory.getRegistry()).thenReturn(registry);

		CachePurgeController controller = new CachePurgeController(registryFactory, assetCache, pageCache);
		ReflectionTestUtils.setField(controller, "assetsCacheSecret", "assets-secret");
		ReflectionTestUtils.setField(controller, "pagesCacheSecret", "pages-secret");
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void assetsPurgePurgesAssetsAndPages() throws Exception {
		mockMvc.perform(purge("/cache/assets/purge", HOST, "assets-secret")).andExpect(status().isNoContent());

		verify(assetCache).invalidate(REPOSITORY_ID);
		verify(pageCache).invalidate(REPOSITORY_ID);
	}

	@Test
	void pagesPurgeOnlyPurgesPages() throws Exception {
		mockMvc.perform(purge("/cache/pages/purge", HOST, "pages-secret")).andExpect(status().isNoContent());

		verify(pageCache).invalidate(REPOSITORY_ID);
		verify(assetCache, never()).invalidate(any());
	}

	@Test
	void rejectsOtherCachesSecret() throws Exception {
		mockMvc.perform(purge("/cache/assets/purge", HOST, "pages-secret")).andExpect(status().isUnauthorized());
		mockMvc.perform(purge("/cache/pages/purge", HOST, "assets-secret")).andExpect(status().isUnauthorized());

		verifyNoInteractions(assetCache, pageCache);
	}

	@Test
	void rejectsMissingAuthorization() throws Exception {
		mockMvc.perform(post("/cache/pages/purge").with(r -> { r.setServerName(HOST); return r; })).andExpect(status().isUnauthorized());

		verifyNoInteractions(pageCache);
	}

	@Test
	void rejectsEverythingWhenSecretNotConfigured() throws Exception {
		CachePurgeController controller = new CachePurgeController(mock(SiteConnectionRegistryFactory.class), assetCache, pageCache);
		ReflectionTestUtils.setField(controller, "pagesCacheSecret", "");
		MockMvc unconfigured = MockMvcBuilders.standaloneSetup(controller).build();

		unconfigured.perform(purge("/cache/pages/purge", HOST, "")).andExpect(status().isUnauthorized());

		verifyNoInteractions(pageCache);
	}

	@Test
	void returnsNotFoundForUnknownHost() throws Exception {
		mockMvc.perform(purge("/cache/pages/purge", "unknown.example.com", "pages-secret")).andExpect(status().isNotFound());

		verifyNoInteractions(pageCache);
	}

	private static MockHttpServletRequestBuilder purge(String path, String host, String token) {
		return post(path)
				.header("Authorization", "Bearer " + token)
				.with(request -> {
					request.setServerName(host);
					return request;
				});
	}
}
