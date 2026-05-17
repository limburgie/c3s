package be.webfactor.c3s.siteassetstore.s3;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record S3Location(String endpointHost, String region, String bucket, String keyPrefix) {

	private static final Pattern AWS_HOST = Pattern.compile("^s3\\.([a-z0-9-]+)\\.amazonaws\\.com$");
	private static final Pattern DO_HOST = Pattern.compile("^([a-z0-9-]+)\\.digitaloceanspaces\\.com$");
	private static final String EXPECTED = "Expected: s3://{s3.<region>.amazonaws.com|<region>.digitaloceanspaces.com}/<bucket>/<key-prefix>/";

	public static S3Location parse(String uri) {
		if (uri == null || !uri.startsWith("s3://")) {
			throw new IllegalArgumentException("S3 repository id must start with s3:// (was: " + uri + "). " + EXPECTED);
		}

		String rest = uri.substring("s3://".length());
		int firstSlash = rest.indexOf('/');

		if (firstSlash < 0) {
			throw new IllegalArgumentException("S3 repository id is missing a bucket: " + uri + ". " + EXPECTED);
		}

		String host = rest.substring(0, firstSlash);
		String afterHost = rest.substring(firstSlash + 1);
		int secondSlash = afterHost.indexOf('/');
		String bucket = secondSlash < 0 ? afterHost : afterHost.substring(0, secondSlash);
		String prefix = secondSlash < 0 ? "" : afterHost.substring(secondSlash + 1);

		if (host.isBlank()) {
			throw new IllegalArgumentException("S3 endpoint host is empty in: " + uri + ". " + EXPECTED);
		}
		if (bucket.isBlank()) {
			throw new IllegalArgumentException("S3 bucket is empty in: " + uri + ". " + EXPECTED);
		}

		String region = extractRegion(host, uri);

		if (!prefix.isEmpty() && !prefix.endsWith("/")) {
			prefix = prefix + "/";
		}

		return new S3Location(host, region, bucket, prefix);
	}

	private static String extractRegion(String host, String uri) {
		Matcher aws = AWS_HOST.matcher(host);
		if (aws.matches()) {
			return aws.group(1);
		}
		Matcher digitalOcean = DO_HOST.matcher(host);
		if (digitalOcean.matches()) {
			return digitalOcean.group(1);
		}
		throw new IllegalArgumentException("S3 endpoint host '" + host + "' is not a recognized regional URL in: " + uri + ". " + EXPECTED);
	}
}
