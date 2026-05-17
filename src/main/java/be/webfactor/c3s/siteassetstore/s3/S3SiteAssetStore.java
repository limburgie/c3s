package be.webfactor.c3s.siteassetstore.s3;

import be.webfactor.c3s.siteassetstore.SiteAssetStoreConnection;
import be.webfactor.c3s.siteassetstore.SiteAssetStoreType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.siteassetstore.SiteAssetNotFoundException;
import be.webfactor.c3s.siteassetstore.filebased.AbstractFileBasedSiteAssetStore;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@Service
@Scope("request")
@RequiredArgsConstructor
public class S3SiteAssetStore extends AbstractFileBasedSiteAssetStore {

	private final S3ClientProvider clientProvider;

	private S3Client s3;
	private String bucket;
	private String keyPrefix;

	public SiteAssetStoreType getType() {
		return SiteAssetStoreType.S3;
	}

	@Override
	public void initialize(SiteAssetStoreConnection connection) {
		S3Location location = S3Location.parse(connection.getRepositoryId());
		this.bucket = location.bucket();
		this.keyPrefix = location.keyPrefix();
		this.s3 = clientProvider.get(connection, location);
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
			throw new SiteAssetNotFoundException("s3://" + bucket + "/" + key, e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
