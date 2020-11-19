package com.zbutwialypiernik.flixage.config;

import org.springframework.web.util.UriComponentsBuilder;

/**
 * Class needed to create dynamic links to resources, using gateway host
 * Will be mostly used in links to thumbnails
 */
@FunctionalInterface
public interface GatewayUriFactory {

    UriComponentsBuilder createUriBuilder();

}
