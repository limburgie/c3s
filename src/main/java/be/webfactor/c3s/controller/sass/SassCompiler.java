package be.webfactor.c3s.controller.sass;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import be.webfactor.c3s.controller.PageController;
import be.webfactor.c3s.siteassetstore.SiteAssetNotFoundException;
import be.webfactor.c3s.siteassetstore.SiteAssetStore;
import io.bit3.jsass.*;
import io.bit3.jsass.Compiler;
import io.bit3.jsass.importer.Import;

public class SassCompiler {

	private static final String ENCODING = StandardCharsets.UTF_8.toString();
	private static final String SYNTHETIC_SCHEME = "c3s://site";

	private final Compiler compiler = new Compiler();
	private final Options options = new Options();

	public SassCompiler(SiteAssetStore siteAssetStore, String originalRelativeDirectory) {
		options.setOutputStyle(OutputStyle.COMPRESSED);
		options.setImporters(Collections.singletonList((importUrl, previous) -> {
			String relativeDir = getRelativeDirectory(originalRelativeDirectory, previous);

			String absoluteUrl = toAbsoluteUrl(relativeDir, importUrl);
			String absolutePartialUrl = toPartialUrl(absoluteUrl);

			return Collections.singletonList(createImport(siteAssetStore, absoluteUrl, absolutePartialUrl));
		}));
	}

	private String getRelativeDirectory(String originalRelativeDirectory, Import previous) {
		String previousAbsoluteUri = previous.getAbsoluteUri().toString();
		String previousUrl = StringUtils.removeStart(previousAbsoluteUri, SYNTHETIC_SCHEME + PageController.ASSETS_PREFIX);

		if (!previousAbsoluteUri.equals("stdin") && !previousUrl.equals(previousAbsoluteUri)) {
			return previousUrl.substring(0, previousUrl.lastIndexOf("/") + 1);
		}
		return originalRelativeDirectory;
	}

	private Import createImport(SiteAssetStore siteAssetStore, String absoluteUrl, String absolutePartialUrl) {
		try {
			return doCreateImport(siteAssetStore, absoluteUrl);
		} catch (SiteAssetNotFoundException e) {
			try {
				return doCreateImport(siteAssetStore, absolutePartialUrl);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Import doCreateImport(SiteAssetStore siteAssetStore, String absoluteUrl) throws IOException {
		try {
			URI importAssetPath = new URI(absoluteUrl);
			String relativePath = StringUtils.removeStart(absoluteUrl, SYNTHETIC_SCHEME + "/");
			String contents = siteAssetStore.readResource(relativePath);

			return new Import(importAssetPath, importAssetPath, contents);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private String toAbsoluteUrl(String relativeDirectory, String url) {
		if (!url.endsWith(".scss") && !url.endsWith(".css")) {
			url += ".scss";
		}

		if (url.startsWith("http")) {
			return url;
		} else if (url.startsWith("/")) {
			return SYNTHETIC_SCHEME + url;
		} else {
			return SYNTHETIC_SCHEME + PageController.ASSETS_PREFIX + relativeDirectory + url;
		}
	}

	private String toPartialUrl(String url) {
		int lastSlashPos = url.lastIndexOf('/');
		String firstPart = url.substring(0, lastSlashPos + 1);
		String lastPart = url.substring(lastSlashPos + 1);

		return firstPart + "_" + lastPart;
	}

	public synchronized byte[] compile(byte[] content) {
		try {
			String stringContent = IOUtils.toString(content, ENCODING);
			Output output = compiler.compileString(stringContent, options);

			return IOUtils.toByteArray(new StringReader(output.getCss()), ENCODING);
		} catch (IOException | CompilationException e) {
			throw new SassCompilationException(e);
		}
	}
}
