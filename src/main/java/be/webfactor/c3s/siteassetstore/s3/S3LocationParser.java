package be.webfactor.c3s.siteassetstore.s3;

import java.util.Optional;

interface S3LocationParser {

	Optional<S3Location> tryParse(String endpointHost, String bucket, String keyPrefix);

	String description();
}
