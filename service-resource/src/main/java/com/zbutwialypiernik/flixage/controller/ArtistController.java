package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.AlbumResponse;
import com.zbutwialypiernik.flixage.dto.ArtistResponse;
import com.zbutwialypiernik.flixage.dto.TrackResponse;
import com.zbutwialypiernik.flixage.entity.Album;
import com.zbutwialypiernik.flixage.entity.Artist;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.service.AlbumService;
import com.zbutwialypiernik.flixage.service.QueryableService;
import com.zbutwialypiernik.flixage.service.TrackService;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/artists")
public class ArtistController extends QueryableController<Artist, ArtistResponse> {

    private final TrackService trackService;
    private final AlbumService albumService;

    private final BoundMapperFacade<Track, TrackResponse> trackMapper;
    private final BoundMapperFacade<Album, AlbumResponse> albumMapper;

    public ArtistController(QueryableService<Artist> service, TrackService trackService, AlbumService albumService, MapperFactory mapperFactory) {
        super(service, mapperFactory);
        this.trackService = trackService;
        this.albumService = albumService;

        this.trackMapper = mapperFactory.getMapperFacade(Track.class, TrackResponse.class);
        this.albumMapper = mapperFactory.getMapperFacade(Album.class, AlbumResponse.class);
    }

    @GetMapping("{id}/singles")
    public List<TrackResponse> getSingles(@PathVariable String id) {
        return trackService.getArtistSingles(id).stream()
                .map(trackMapper::map)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}/albums")
    public List<AlbumResponse> getAlbums(@PathVariable String id) {
        return albumService.getByArtistId(id).stream()
                .map(albumMapper::map)
                .collect(Collectors.toList());
    }

}
