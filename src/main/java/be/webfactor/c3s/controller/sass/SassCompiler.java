package be.webfactor.c3s.controller.sass;

import be.webfactor.c3s.controller.PageController;
import be.webfactor.c3s.siteassetstore.SiteAssetNotFoundException;
import be.webfactor.c3s.siteassetstore.SiteAssetStore;
import com.sass_lang.embedded_protocol.InboundMessage.ImportResponse.ImportSuccess;
import com.sass_lang.embedded_protocol.OutputStyle;
import com.sass_lang.embedded_protocol.Syntax;
import de.larsgrefer.sass.embedded.SassCompilationFailedException;
import de.larsgrefer.sass.embedded.SassCompilerFactory;
import de.larsgrefer.sass.embedded.importer.CustomImporter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class SassCompiler {

	private static final String SCHEME = "c3s:";

	public byte[] compile(SiteAssetStore siteAssetStore, String originalRelativeDirectory, byte[] scss) {
		String source = new String(scss, StandardCharsets.UTF_8);

		try (de.larsgrefer.sass.embedded.SassCompiler compiler = SassCompilerFactory.bundled()) {
			compiler.registerImporter(new SiteAssetStoreImporter(siteAssetStore, originalRelativeDirectory));

			return compiler.compileString(source, Syntax.SCSS, OutputStyle.COMPRESSED)
					.getCss().getBytes(StandardCharsets.UTF_8);
		} catch (SassCompilationFailedException | IOException e) {
			throw new SassCompilationException(e);
		}
	}

	private static class SiteAssetStoreImporter extends CustomImporter {

		private final SiteAssetStore siteAssetStore;
		private final String baseDir;

		SiteAssetStoreImporter(SiteAssetStore siteAssetStore, String originalRelativeDirectory) {
			this.siteAssetStore = siteAssetStore;
			this.baseDir = stripLeadingSlash(PageController.ASSETS_PREFIX + originalRelativeDirectory);
		}

		@Override
		public String canonicalize(String url, boolean fromImport) {
			String sitePath = toSitePath(url);

			for (String candidate : List.of(sitePath, partialOf(sitePath))) {
				if (exists(candidate)) {
					return SCHEME + "/" + candidate;
				}
			}

			return null;
		}

		@Override
		public ImportSuccess handleImport(String url) {
			String contents = siteAssetStore.readResource(stripScheme(url));

			return ImportSuccess.newBuilder().setContents(contents).setSyntax(Syntax.SCSS).build();
		}

		private String toSitePath(String url) {
			if (url.startsWith(SCHEME)) {
				return ensureScss(stripScheme(url));
			}

			String withScss = ensureScss(url);
			return withScss.startsWith("/") ? stripLeadingSlash(withScss) : baseDir + withScss;
		}

		private boolean exists(String sitePath) {
			try {
				siteAssetStore.readResource(sitePath);
				return true;
			} catch (SiteAssetNotFoundException e) {
				return false;
			}
		}

		private static String ensureScss(String url) {
			return url.endsWith(".scss") || url.endsWith(".css") ? url : url + ".scss";
		}

		private static String partialOf(String path) {
			int lastSlash = path.lastIndexOf('/');
			return path.substring(0, lastSlash + 1) + "_" + path.substring(lastSlash + 1);
		}

		private static String stripScheme(String url) {
			return stripLeadingSlash(url.substring(SCHEME.length()));
		}

		private static String stripLeadingSlash(String path) {
			return path.startsWith("/") ? path.substring(1) : path;
		}
	}
}
