package be.webfactor.c3s.siteassetstore.s3;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AwsS3LocationParser implements S3LocationParser {

	private static final Pattern HOST = Pattern.compile("^s3\\.([a-z0-9-]+)\\.amazonaws\\.com$");

	public Optional<S3Location> tryParse(String endpointHost, String bucket, String keyPrefix) {
		Matcher matcher = HOST.matcher(endpointHost);
		return matcher.matches() ? Optional.of(new S3Location(endpointHost, matcher.group(1), bucket, keyPrefix)) : Optional.empty();
	}

	public String description() {
		return "s3.<region>.amazonaws.com";
	}
}
