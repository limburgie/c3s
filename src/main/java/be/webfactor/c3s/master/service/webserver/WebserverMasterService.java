package be.webfactor.c3s.master.service.webserver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.master.service.MasterAssetNotFoundException;
import be.webfactor.c3s.master.service.filebased.AbstractFileBasedMasterService;
import be.webfactor.c3s.repository.RepositoryType;

@Service
@Scope("request")
public class WebserverMasterService extends AbstractFileBasedMasterService {

	public RepositoryType getType() {
		return RepositoryType.WEB_SERVER;
	}

	protected String doReadResource(String relativePath) {
		try {
			return IOUtils.toString(uri(relativePath), Charset.defaultCharset());
		} catch (FileNotFoundException e) {
			throw new MasterAssetNotFoundException(uri(relativePath).toString(), e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected byte[] doReadAssetBytes(String relativePath) {
		try {
			return IOUtils.toByteArray(uri(relativePath).toURL());
		} catch (FileNotFoundException e) {
			throw new MasterAssetNotFoundException(uri(relativePath).toString(), e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private URI uri(String relativePath) {
		try {
			return new URI(getBaseUrl() + "/" + relativePath);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
