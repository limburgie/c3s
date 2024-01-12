package be.webfactor.c3s.controller.helper.asset;

import lombok.Value;
import org.springframework.http.MediaType;

@Value
public class Asset {

    byte[] data;
    MediaType contentType;
}
