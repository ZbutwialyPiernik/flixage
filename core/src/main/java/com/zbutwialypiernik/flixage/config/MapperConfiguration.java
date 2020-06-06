package com.zbutwialypiernik.flixage.config;

import lombok.extern.log4j.Log4j2;
import ma.glasnost.orika.Converter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Log4j2
@Configuration
public class MapperConfiguration {

    @Bean
    public MapperFactory mapperFactory(List<Converter<?, ?>> converters) {
        MapperFactory factory = new DefaultMapperFactory.Builder()
                .build();

        log.info("Registering: " + converters.size() + " custom converters");

        ConverterFactory converterFactory = factory.getConverterFactory();

        converters.forEach(converterFactory::registerConverter);

        return factory;
    }

    @Bean
    public MapperFacade mapperFacade(MapperFactory mapperFactory) {
        return mapperFactory.getMapperFacade();
    }

}
