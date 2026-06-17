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
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
public class SassCompiler {

	/** Synthetic scheme for canonical URLs of stylesheets resolved from the site asset store. */
	private static final String SCHEME = "c3s:";

	private static final ThreadLocal<CompileContext> CONTEXT = new ThreadLocal<>();

	private de.larsgrefer.sass.embedded.SassCompiler compiler;

	public synchronized byte[] compile(SiteAssetStore siteAssetStore, String originalRelativeDirectory, byte[] scss) {
		String source = new String(scss, StandardCharsets.UTF_8);
		String baseDir = stripLeadingSlash(PageController.ASSETS_PREFIX + originalRelativeDirectory);

		CONTEXT.set(new CompileContext(siteAssetStore, baseDir));
		try {
			return doCompile(source);
		} finally {
			CONTEXT.remove();
		}
	}

	@EventListener(ApplicationReadyEvent.class)
	synchronized void warmUp() {
		try {
			compileWith(compiler(), "a{b:c}");
		} catch (IOException | RuntimeException e) {
			log.warn("SASS compiler warm-up failed; the first compile will pay the startup cost", e);
		}
	}

	@PreDestroy
	synchronized void shutdown() {
		disposeCompiler();
	}

	private byte[] doCompile(String source) {
		try {
			return compileWith(compiler(), source);
		} catch (IOException e) {
			// The long-lived subprocess may have died; rebuild it once and retry.
			log.warn("SASS compile failed with an I/O error; recreating the compiler and retrying", e);
			disposeCompiler();
			try {
				return compileWith(compiler(), source);
			} catch (IOException retry) {
				throw new SassCompilationException(retry);
			}
		}
	}

	private byte[] compileWith(de.larsgrefer.sass.embedded.SassCompiler compiler, String source) throws IOException {
		try {
			return compiler.compileString(source, Syntax.SCSS, OutputStyle.COMPRESSED)
					.getCss().getBytes(StandardCharsets.UTF_8);
		} catch (SassCompilationFailedException e) {
			throw new SassCompilationException(e);
		}
	}

	private de.larsgrefer.sass.embedded.SassCompiler compiler() throws IOException {
		if (compiler == null) {
			de.larsgrefer.sass.embedded.SassCompiler created = SassCompilerFactory.bundled();
			created.registerImporter(new SiteAssetStoreImporter());
			compiler = created;
		}
		return compiler;
	}

	private void disposeCompiler() {
		if (compiler != null) {
			try {
				compiler.close();
			} catch (Exception ignored) {
				// best-effort; we are discarding it anyway
			}
			compiler = null;
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

	private record CompileContext(SiteAssetStore siteAssetStore, String baseDir) {}

	private static class SiteAssetStoreImporter extends CustomImporter {

		@Override
		public String canonicalize(String url, boolean fromImport) {
			CompileContext context = CONTEXT.get();
			if (context == null) {
				return null;
			}

			String sitePath = toSitePath(context, url);

			for (String candidate : List.of(sitePath, partialOf(sitePath))) {
				if (exists(context.siteAssetStore(), candidate)) {
					return SCHEME + "/" + candidate;
				}
			}

			return null;
		}

		@Override
		public ImportSuccess handleImport(String url) {
			String contents = CONTEXT.get().siteAssetStore().readResource(stripScheme(url));

			return ImportSuccess.newBuilder().setContents(contents).setSyntax(Syntax.SCSS).build();
		}

		private String toSitePath(CompileContext context, String url) {
			if (url.startsWith(SCHEME)) {
				return ensureScss(stripScheme(url));
			}

			String withScss = ensureScss(url);
			return withScss.startsWith("/") ? stripLeadingSlash(withScss) : context.baseDir() + withScss;
		}

		private boolean exists(SiteAssetStore siteAssetStore, String sitePath) {
			try {
				siteAssetStore.readResource(sitePath);
				return true;
			} catch (SiteAssetNotFoundException e) {
				return false;
			}
		}
	}
}
