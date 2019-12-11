package be.webfactor.c3s.controller.sass;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.apache.commons.io.IOUtils;

import be.webfactor.c3s.controller.PageController;
import io.bit3.jsass.*;
import io.bit3.jsass.Compiler;
import io.bit3.jsass.importer.Import;

public class SassCompiler {

	private static final String ENCODING = StandardCharsets.UTF_8.toString();

	private Compiler compiler = new Compiler();
	private Options options = new Options();

	public SassCompiler(String basePath, String relativeDirectory) {
		options.setOutputStyle(OutputStyle.COMPRESSED);
		options.setImporters(Collections.singletonList((importUrl, previous) -> {
			String absoluteUrl = toAbsoluteUrl(basePath, relativeDirectory, importUrl);
			String absolutePartialUrl = toPartialUrl(absoluteUrl);

			return Collections.singletonList(createImport(absoluteUrl, absolutePartialUrl));
		}));
	}

	private Import createImport(String absoluteUrl, String absolutePartialUrl) {
		try {
			return doCreateImport(absoluteUrl);
		} catch(FileNotFoundException e) {
			try {
				return doCreateImport(absolutePartialUrl);
			} catch (IOException ex) {
				throw new RuntimeException(e);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Import doCreateImport(String url) throws IOException {
		try {
			URI importAssetPath = new URI(url);
			String contents = IOUtils.toString(importAssetPath, StandardCharsets.UTF_8);

			return new Import(importAssetPath, importAssetPath, contents);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private String toAbsoluteUrl(String basePath, String relativeDirectory, String url) {
		if (!url.endsWith(".scss")) {
			url += ".scss";
		}

		if (url.startsWith("http")) {
			return url;
		} else if (url.startsWith("/")) {
			return basePath + url;
		} else {
			return basePath + PageController.ASSETS_PREFIX + relativeDirectory + url;
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
