package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.TrackResponse;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.service.TrackService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tracks")
public class TrackController extends QueryableController<Track, TrackResponse>{

    private final TrackService trackService;

    private final MapperFacade mapper;

    public TrackController(TrackService trackService, MapperFacade mapper) {
        super(trackService);
        this.mapper = mapper;
        this.trackService = trackService;
    }

    @Override
    public TrackResponse toResponse(Track track) {
        return mapper.map(track, TrackResponse.class);
    }

}
