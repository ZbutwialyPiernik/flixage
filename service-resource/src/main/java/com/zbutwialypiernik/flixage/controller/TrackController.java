package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.TrackResponse;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationToken;
import com.zbutwialypiernik.flixage.service.TrackService;
import com.zbutwialypiernik.flixage.service.resource.track.AudioResource;
import ma.glasnost.orika.MapperFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/tracks")
public class TrackController extends QueryableController<Track, TrackResponse>{

    private final TrackService service;

    public TrackController(TrackService trackService, MapperFactory mapperFactory) {
        super(trackService, mapperFactory);
        this.service = trackService;
    }

    @Override
    public byte[] getThumbnail(String id) {
        return super.getThumbnail(id);
    }

    @GetMapping("{id}/stream")
    public void streamTrack(@PathVariable String id, HttpServletResponse response) throws IOException {
        AudioResource resource = service.getTrackFile(id);

        response.setContentType(resource.getMimeType());
        response.getOutputStream().write(resource.getContent());
    }

    @PostMapping("{id}/streamCount")
    public void increaseStreamCount(@PathVariable String id, @AuthenticationPrincipal JwtAuthenticationToken principal) {
        service.increaseStreamCount(principal.getId(), id);
    }

}
