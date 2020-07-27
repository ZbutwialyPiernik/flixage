package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.TrackResponse;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.exception.ResourceLoadingException;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationToken;
import com.zbutwialypiernik.flixage.service.TrackService;
import com.zbutwialypiernik.flixage.service.TrackStreamService;
import com.zbutwialypiernik.flixage.service.resource.image.ImageResource;
import com.zbutwialypiernik.flixage.service.resource.track.AudioResource;
import lombok.extern.log4j.Log4j2;
import ma.glasnost.orika.MapperFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@RestController
@RequestMapping("/tracks")
public class TrackController extends QueryableController<Track, TrackResponse>{

    private final TrackService service;
    private final TrackStreamService streamService;

    public TrackController(TrackService trackService, TrackStreamService streamService, MapperFactory mapperFactory) {
        super(trackService, mapperFactory);
        this.service = trackService;
        this.streamService = streamService;
    }

    @Override
    @GetMapping("{id}")
    public TrackResponse getById(@PathVariable String id) {
        var track = service.findById(id).orElseThrow(ResourceNotFoundException::new);

        if (track.getAudioFile() == null) {
            throw new ResourceNotFoundException();
        }

        return dtoMapper.map(track);
    }

    @Override
    @GetMapping("{id}/thumbnail")
    public byte[] getThumbnail(@PathVariable String id) {
        try {
            return service.getThumbnailById(id, true).map(ImageResource::getInputStream).orElseThrow(ResourceNotFoundException::new).readAllBytes();
        } catch (IOException e) {
            log.error("Error during loading of thumbnail", e);
            throw new ResourceLoadingException("Error during loading of thumbnail");
        }
    }

    @GetMapping("{id}/stream")
    public void streamTrack(@PathVariable String id, HttpServletResponse response) throws IOException {
        AudioResource resource = service.getTrackFile(id);

        response.setContentType(resource.getMimeType());
        response.getOutputStream().write(resource.getContent());
    }

    @PostMapping("{id}/streamCount")
    public void increaseStreamCount(@PathVariable String id, @AuthenticationPrincipal JwtAuthenticationToken principal) {
        streamService.increaseStreamCount(principal.getId(), id);
    }

}
