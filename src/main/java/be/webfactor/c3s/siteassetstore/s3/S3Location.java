package be.webfactor.c3s.siteassetstore.s3;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record S3Location(String endpointHost, String region, String bucket, String keyPrefix) {

	private static final List<S3LocationParser> PARSERS = List.of(
			new AwsS3LocationParser(),
			new DigitalOceanS3LocationParser()
	);

	public static S3Location parse(String uri) {
		if (uri == null || !uri.startsWith("s3://")) {
			throw new IllegalArgumentException("S3 repository id must start with s3:// (was: " + uri + "). " + expected());
		}

		String rest = uri.substring("s3://".length());
		int firstSlash = rest.indexOf('/');

		if (firstSlash < 0) {
			throw new IllegalArgumentException("S3 repository id is missing a bucket: " + uri + ". " + expected());
		}

		String host = rest.substring(0, firstSlash);
		String afterHost = rest.substring(firstSlash + 1);
		int secondSlash = afterHost.indexOf('/');
		String bucket = secondSlash < 0 ? afterHost : afterHost.substring(0, secondSlash);
		String prefix = secondSlash < 0 ? "" : afterHost.substring(secondSlash + 1);

		if (host.isBlank()) {
			throw new IllegalArgumentException("S3 endpoint host is empty in: " + uri + ". " + expected());
		}
		if (bucket.isBlank()) {
			throw new IllegalArgumentException("S3 bucket is empty in: " + uri + ". " + expected());
		}

		if (!prefix.isEmpty() && !prefix.endsWith("/")) {
			prefix = prefix + "/";
		}

		String keyPrefix = prefix;
		return PARSERS.stream()
				.map(parser -> parser.tryParse(host, bucket, keyPrefix))
				.flatMap(Optional::stream)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
						"S3 endpoint host '" + host + "' is not a recognized regional URL in: " + uri + ". " + expected()));
	}

	private static String expected() {
		String hosts = PARSERS.stream().map(S3LocationParser::description).collect(Collectors.joining("|"));
		return "Expected: s3://{" + hosts + "}/<bucket>/<key-prefix>/";
	}
}
