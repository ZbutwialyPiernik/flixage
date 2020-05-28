package com.zbutwialypiernik.flixage.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zbutwialypiernik.flixage.config.mapping.CaseInsensitiveEnumEditor;
import com.zbutwialypiernik.flixage.entity.QueryableType;
import lombok.extern.log4j.Log4j2;
import ma.glasnost.orika.Converter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.util.List;

@Log4j2
@Configuration
public class MapperConfiguration {

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
