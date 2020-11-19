package com.zbutwialypiernik.flixage.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ServletUriFactory implements GatewayUriFactory {

    @Override
    public UriComponentsBuilder createUriBuilder() {
        return  ServletUriComponentsBuilder.fromCurrentContextPath();
    }

}
