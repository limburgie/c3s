package be.webfactor.c3s.master.service.s3;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import be.webfactor.c3s.repository.RepositoryConnection;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

@Component
public class S3ClientProvider implements DisposableBean {

	private final Map<ClientKey, S3Client> clients = new ConcurrentHashMap<>();

	public S3Client get(RepositoryConnection connection) {
		if (connection.getRegion() == null || connection.getRegion().isBlank()) {
			throw new IllegalStateException("S3 repository requires a region; configure the region property for the site (received null/blank)");
		}

		ClientKey key = new ClientKey(connection.getAccessToken(), connection.getSecretKey(), connection.getRegion());
		return clients.computeIfAbsent(key, this::build);
	}

	private S3Client build(ClientKey key) {
		S3ClientBuilder builder = S3Client.builder().region(Region.of(key.region()));

		if (key.accessKey() != null && !key.accessKey().isBlank() && key.secretKey() != null && !key.secretKey().isBlank()) {
			builder.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(key.accessKey(), key.secretKey())));
		}

		return builder.build();
	}

	public void destroy() {
		clients.values().forEach(S3Client::close);
		clients.clear();
	}

	private record ClientKey(String accessKey, String secretKey, String region) {}
}
