package com.zbutwialypiernik.flixage.config;

import lombok.extern.log4j.Log4j2;
import ma.glasnost.orika.Converter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.List;

@Log4j2
@Configuration
public class OrikaConfiguration {

    @Bean
    public MapperFactory mapperFactory(List<Converter<?, ?>> converters) {
        MapperFactory factory = new DefaultMapperFactory.Builder()
                .build();

        log.info("Registering: " + converters.size() + " custom converters");

        converters.forEach((converter) -> factory.getConverterFactory().registerConverter(converter));

        return factory;
    }

    @Bean
    public MapperFacade mapperFacade(MapperFactory mapperFactory) {
        return mapperFactory.getMapperFacade();
    }

}
