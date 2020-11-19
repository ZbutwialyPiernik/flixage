package com.zbutwialypiernik.flixage.dto.mapper;

import com.zbutwialypiernik.flixage.config.GatewayUriFactory;
import com.zbutwialypiernik.flixage.dto.*;
import com.zbutwialypiernik.flixage.dto.mapper.converter.ThumbnailUrlConverter;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistResponse;
import com.zbutwialypiernik.flixage.entity.*;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class DtoMappersConfiguration {

    private final MapperFactory mapperFactory;

    private final GatewayUriFactory uriBuilder;
    
    public DtoMappersConfiguration(MapperFactory mapperFactory, GatewayUriFactory uriBuilder) {
        this.mapperFactory = mapperFactory;
        this.uriBuilder = uriBuilder;
    }

    protected <A extends Queryable, B extends QueryableResponse> ClassMapBuilder<A, B> createCustomMapping(Class<A> entityClass, Class<B> responseClass, String resourcePath) {
        return mapperFactory.classMap(entityClass, responseClass)
                .customize(new ThumbnailUrlConverter<>(uriBuilder, resourcePath))
                .byDefault();
    }

    @PostConstruct
    public void init() {
        createCustomMapping(Playlist.class, PlaylistResponse.class, "playlists").register();
        createCustomMapping(Album.class, AlbumResponse.class, "albums").register();
        createCustomMapping(User.class, UserResponse.class, "users").register();
        createCustomMapping(Artist.class, ArtistResponse.class, "artists").register();
        createCustomMapping(Track.class, TrackResponse.class, "tracks")
                .fieldMap("audioFile.duration", "duration").add()
                .register();
    }

}
