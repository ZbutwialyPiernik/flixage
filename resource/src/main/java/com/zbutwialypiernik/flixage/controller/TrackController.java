package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.TrackResponse;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.service.TrackService;
import ma.glasnost.orika.MapperFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * In early version audio will be exposed as normal file, in later version ill try to make things like DRM etc.
 */
@RestController
@RequestMapping("/tracks")
public class TrackController extends QueryableController<Track, TrackResponse>{

    public TrackController(TrackService trackService, MapperFactory mapperFactory) {
        super(trackService, mapperFactory);
    }


}
