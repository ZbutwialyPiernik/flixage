package com.zbutwialypiernik.flixage.dto.mapper;

import com.zbutwialypiernik.flixage.config.GatewayUriBuilder;
import com.zbutwialypiernik.flixage.dto.ArtistResponse;
import com.zbutwialypiernik.flixage.dto.QueryableResponse;
import com.zbutwialypiernik.flixage.dto.TrackResponse;
import com.zbutwialypiernik.flixage.dto.UserResponse;
import com.zbutwialypiernik.flixage.dto.mapper.converter.ThumbnailUrlConverter;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistResponse;
import com.zbutwialypiernik.flixage.entity.*;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DtoMappersConfiguration {

    private final MapperFactory mapperFactory;

    private final GatewayUriBuilder gatewayUriBuilder;

    public DtoMappersConfiguration(MapperFactory mapperFactory, GatewayUriBuilder gatewayUriBuilder) {
        this.mapperFactory = mapperFactory;
        this.gatewayUriBuilder = gatewayUriBuilder;

        createCustomMapping(Playlist.class, PlaylistResponse.class, "playlists").register();
        createCustomMapping(User.class, UserResponse.class, "users").register();
        createCustomMapping(Track.class, TrackResponse.class, "tracks").register();
        createCustomMapping(Artist.class, ArtistResponse.class, "artists").register();
    }
    public <A extends Queryable, B extends QueryableResponse> ClassMapBuilder<A, B> createCustomMapping(Class<A> entityClass, Class<B> responseClass, String resourcePath) {
        return mapperFactory.classMap(entityClass, responseClass)
                .customize(new ThumbnailUrlConverter<>(gatewayUriBuilder, resourcePath))
                .byDefault();
    }

}
