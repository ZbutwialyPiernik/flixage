package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.AlbumResponse;
import com.zbutwialypiernik.flixage.dto.PageResponse;
import com.zbutwialypiernik.flixage.dto.TrackResponse;
import com.zbutwialypiernik.flixage.entity.Album;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.service.AlbumService;
import com.zbutwialypiernik.flixage.service.QueryableService;
import com.zbutwialypiernik.flixage.service.TrackService;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/albums")
public class AlbumController extends QueryableController<Album, AlbumResponse>  {

    private final TrackService trackService;

    private final BoundMapperFacade<Track, TrackResponse> trackMapper;

    public AlbumController(QueryableService<Album> service, TrackService trackService, MapperFactory mapperFactory) {
        super(service, mapperFactory);
        this.trackService = trackService;
        this.trackMapper = mapperFactory.getMapperFacade(Track.class, TrackResponse.class);
    }

    @GetMapping("/recent")
    public PageResponse<AlbumResponse> getRecentlyAddedTrack(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit) {
        final var page = getService().getRecentlyAdded(offset, limit);

        return new PageResponse<>(page
                .map(dtoMapper::map)
                .toList(), page.getTotalElements());
    }

    @GetMapping("/{id}/tracks")
    public List<TrackResponse> getTracks(@PathVariable String id) {
        return trackService.getTracksByAlbumId(id, true).stream()
                .map(trackMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public AlbumService getService() {
        return (AlbumService) super.getService();
    }

}
