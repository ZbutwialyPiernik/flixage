package com.zbutwialypiernik.flixage.dto.mapper.converter;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Optional;

@Configuration
public class CustomConverterConfiguration {

    @Bean
    public BidirectionalConverter<Duration, Long> durationConverter() {
        return new BidirectionalConverter<>() {
            @Override
            public Long convertTo(Duration source, Type<Long> destinationType, MappingContext mappingContext) {
                return source.toMillis();
            }

            @Override
            public Duration convertFrom(Long source, Type<Duration> destinationType, MappingContext mappingContext) {
                return Duration.ofMillis(Optional.ofNullable(source).orElse(0L));
            }
        };
    }

}
