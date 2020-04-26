package com.zbutwialypiernik.flixage.config;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

@Configuration
@Getter
@RequiredArgsConstructor
public class GatewayConfiguration {

    @Value("${GATEWAY_HOSTNAME}")
    private final String hostname;

    @Value("${GATEWAY_PORT}")
    private final String port;

}
