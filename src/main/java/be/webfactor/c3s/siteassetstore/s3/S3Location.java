package be.webfactor.c3s.siteassetstore.s3;

public record S3Location(String bucket, String keyPrefix) {

	public static S3Location parse(String uri) {
		if (uri == null || !uri.startsWith("s3://")) {
			throw new IllegalArgumentException("S3 repository id must start with s3:// (was: " + uri + ")");
		}

		String rest = uri.substring("s3://".length());
		int slash = rest.indexOf('/');

		if (slash < 0) {
			return new S3Location(rest, "");
		}

		String bucket = rest.substring(0, slash);
		String prefix = rest.substring(slash + 1);

		if (!prefix.isEmpty() && !prefix.endsWith("/")) {
			prefix = prefix + "/";
		}

		return new S3Location(bucket, prefix);
	}
}
