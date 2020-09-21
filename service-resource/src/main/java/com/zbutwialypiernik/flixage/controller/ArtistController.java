package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.AlbumResponse;
import com.zbutwialypiernik.flixage.dto.ArtistResponse;
import com.zbutwialypiernik.flixage.dto.PageResponse;
import com.zbutwialypiernik.flixage.dto.TrackResponse;
import com.zbutwialypiernik.flixage.entity.Album;
import com.zbutwialypiernik.flixage.entity.Artist;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.service.AlbumService;
import com.zbutwialypiernik.flixage.service.ArtistService;
import com.zbutwialypiernik.flixage.service.TrackService;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/artists")
public class ArtistController extends QueryableController<Artist, ArtistResponse> {

    private final TrackService trackService;
    private final AlbumService albumService;

    private final BoundMapperFacade<Track, TrackResponse> trackMapper;
    private final BoundMapperFacade<Album, AlbumResponse> albumMapper;

    public ArtistController(ArtistService service, TrackService trackService, AlbumService albumService, MapperFactory mapperFactory) {
        super(service, mapperFactory);
        this.trackService = trackService;
        this.albumService = albumService;

        this.trackMapper = mapperFactory.getMapperFacade(Track.class, TrackResponse.class);
        this.albumMapper = mapperFactory.getMapperFacade(Album.class, AlbumResponse.class);
    }

    @GetMapping("{id}/singles")
    public List<TrackResponse> getSingles(@PathVariable String id) {
        return trackService.getArtistSingles(id, true).stream()
                .map(trackMapper::map)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}/albums")
    public List<AlbumResponse> getAlbums(@PathVariable String id) {
        return albumService.getByArtistId(id).stream()
                .map(albumMapper::map)
                .collect(Collectors.toList());
    }

    @GetMapping("/recent")
    public PageResponse<ArtistResponse> getRecentlyAddedTrack(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit) {
        final var page = getService().getRecentlyAdded(offset, limit);

        return new PageResponse<>(page
                .map(dtoMapper::map)
                .toList(), page.getTotalElements());
    }

    @Override
    public ArtistService getService() {
        return (ArtistService) super.getService();
    }
}
