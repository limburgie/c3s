package be.webfactor.c3s.siteassetstore.s3;

import be.webfactor.c3s.siteassetstore.SiteAssetStoreConnection;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

@Component
public class S3ClientProvider implements DisposableBean {

	private final Map<ClientKey, S3Client> clients = new ConcurrentHashMap<>();

	public S3Client get(SiteAssetStoreConnection connection, S3Location location) {
		ClientKey key = new ClientKey(location.endpointHost(), connection.getAccessToken(), connection.getSecretKey(), location.region());
		return clients.computeIfAbsent(key, this::build);
	}

	private S3Client build(ClientKey key) {
		S3ClientBuilder builder = S3Client.builder().region(Region.of(key.region()));

		if (key.accessKey() != null && !key.accessKey().isBlank() && key.secretKey() != null && !key.secretKey().isBlank()) {
			builder.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(key.accessKey(), key.secretKey())));
		}

		if (!key.endpointHost().endsWith("amazonaws.com")) {
			builder.endpointOverride(URI.create("https://" + key.endpointHost()));
			builder.forcePathStyle(true);
		}

		return builder.build();
	}

	public void destroy() {
		clients.values().forEach(S3Client::close);
		clients.clear();
	}

	private record ClientKey(String endpointHost, String accessKey, String secretKey, String region) {}
}
