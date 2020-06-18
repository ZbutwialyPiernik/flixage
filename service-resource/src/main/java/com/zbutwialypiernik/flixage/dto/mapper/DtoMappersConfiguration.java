package com.zbutwialypiernik.flixage.dto.mapper;

import com.zbutwialypiernik.flixage.config.GatewayUriFactory;
import com.zbutwialypiernik.flixage.dto.*;
import com.zbutwialypiernik.flixage.dto.mapper.converter.ThumbnailUrlConverter;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistResponse;
import com.zbutwialypiernik.flixage.entity.*;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DtoMappersConfiguration {

    private final MapperFactory mapperFactory;

    private final GatewayUriFactory gatewayUriFactory;

    // TODO: make this process fully automatic and more flexible
    public DtoMappersConfiguration(MapperFactory mapperFactory, GatewayUriFactory gatewayUriFactory) {
        this.mapperFactory = mapperFactory;
        this.gatewayUriFactory = gatewayUriFactory;

        createCustomMapping(Playlist.class, PlaylistResponse.class, "playlists").register();
        createCustomMapping(Album.class, AlbumResponse.class, "albums").register();
        createCustomMapping(User.class, UserResponse.class, "users").register();
        createCustomMapping(Artist.class, ArtistResponse.class, "artists").register();
        createCustomMapping(Track.class, TrackResponse.class, "tracks")
                .fieldMap("audioFile.duration", "duration").add()
                .register();
    }
    
    protected <A extends Queryable, B extends QueryableResponse> ClassMapBuilder<A, B> createCustomMapping(Class<A> entityClass, Class<B> responseClass, String resourcePath) {
        return mapperFactory.classMap(entityClass, responseClass)
                .customize(new ThumbnailUrlConverter<>(gatewayUriFactory, resourcePath))
                .byDefault();
    }

}
