package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.TrackResponse;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.service.TrackService;
import ma.glasnost.orika.MapperFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * In early version audio will be exposed as stream, in later version ill try to make things like DRM etc.
 */
@RestController
@RequestMapping("/tracks")
public class TrackController extends QueryableController<Track, TrackResponse>{

    private final TrackService service;

    public TrackController(TrackService trackService, MapperFactory mapperFactory) {
        super(trackService, mapperFactory);
        this.service = trackService;
    }

    @GetMapping("{id}/stream")
    public void streamTrack(@PathVariable String id, HttpServletResponse response) throws IOException {
        response.getOutputStream().write(service.getTrackFile(id));
    }


}
