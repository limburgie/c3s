package be.webfactor.c3s.controller.helper.asset;

import org.springframework.http.MediaType;

public record Asset(byte[] data, MediaType contentType) {

}
