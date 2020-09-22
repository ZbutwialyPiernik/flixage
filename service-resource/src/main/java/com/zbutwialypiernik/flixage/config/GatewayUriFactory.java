package com.zbutwialypiernik.flixage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Class needed to create dynamic links to resources, using gateway host
 * Will be mostly used in links to thumbnails
 */
@Component
public class GatewayUriFactory {

    public UriComponentsBuilder newBuilder() {
        return  ServletUriComponentsBuilder.fromCurrentContextPath();
    }

}
