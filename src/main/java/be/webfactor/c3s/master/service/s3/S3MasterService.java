package be.webfactor.c3s.master.service.s3;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.master.service.MasterAssetNotFoundException;
import be.webfactor.c3s.master.service.filebased.AbstractFileBasedMasterService;
import be.webfactor.c3s.repository.RepositoryConnection;
import be.webfactor.c3s.repository.RepositoryType;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@Service
@Scope("request")
public class S3MasterService extends AbstractFileBasedMasterService {

	@Autowired private S3ClientProvider clientProvider;

	private S3Client s3;
	private String bucket;
	private String keyPrefix;

	public RepositoryType getType() {
		return RepositoryType.S3;
	}

	@Override
	public void initialize(RepositoryConnection connection) {
		S3Location location = S3Location.parse(connection.getRepositoryId());
		this.bucket = location.bucket();
		this.keyPrefix = location.keyPrefix();
		this.s3 = clientProvider.get(connection);
		super.initialize(connection);
	}

	protected String doReadResource(String relativePath) {
		return new String(doReadAssetBytes(relativePath), StandardCharsets.UTF_8);
	}

	protected byte[] doReadAssetBytes(String relativePath) {
		String key = keyPrefix + relativePath;
		try (ResponseInputStream<GetObjectResponse> stream = s3.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build())) {
			return stream.readAllBytes();
		} catch (NoSuchKeyException e) {
			throw new MasterAssetNotFoundException("s3://" + bucket + "/" + key, e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
