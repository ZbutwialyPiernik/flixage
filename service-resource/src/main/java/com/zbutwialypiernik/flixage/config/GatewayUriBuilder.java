package com.zbutwialypiernik.flixage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Class needed to create dynamic links to resources, using gateway host
 * Will be mostly used in links to thumbnails
 */
@Component
public class GatewayUriBuilder {

    private final UriComponentsBuilder builder;

    public GatewayUriBuilder(@Value("${GATEWAY_SERVICE_URL}") String baseUrl) {
        builder = UriComponentsBuilder.newInstance();

        try {
            URI uri = new URI(baseUrl);
            builder.uri(uri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid base URL");
        }
    }

    public UriComponentsBuilder newBuilder() {
        return builder.cloneBuilder();
    }

}
