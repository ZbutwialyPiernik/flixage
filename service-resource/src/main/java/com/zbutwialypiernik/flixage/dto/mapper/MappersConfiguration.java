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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappersConfiguration {

    private final MapperFactory mapperFactory;

    private final GatewayUriBuilder gatewayUriBuilder;

    public MappersConfiguration(MapperFactory mapperFactory, GatewayUriBuilder gatewayUriBuilder) {
        this.mapperFactory = mapperFactory;
        this.gatewayUriBuilder = gatewayUriBuilder;

        createCustomMapping(Playlist.class, PlaylistResponse.class, "playlists");
        createCustomMapping(User.class, UserResponse.class, "users");
        createCustomMapping(Track.class, TrackResponse.class, "tracks");
        createCustomMapping(Artist.class, ArtistResponse.class, "artists");
    }

    public void createCustomMapping(Class<? extends Queryable> entityClass, Class<? extends QueryableResponse> responseClass, String resourcePath) {
        mapperFactory.classMap(entityClass, responseClass)
                .customize(new ThumbnailUrlConverter<>(gatewayUriBuilder, resourcePath))
                .byDefault()
                .register();
    }

}
