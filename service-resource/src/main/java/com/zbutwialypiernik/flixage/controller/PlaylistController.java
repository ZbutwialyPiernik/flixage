package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.TrackResponse;
import com.zbutwialypiernik.flixage.dto.playlist.IdsRequest;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistRequest;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistResponse;
import com.zbutwialypiernik.flixage.entity.Playlist;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.*;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationToken;
import com.zbutwialypiernik.flixage.security.AbstractAuthentication;
import com.zbutwialypiernik.flixage.service.PlaylistService;
import com.zbutwialypiernik.flixage.service.UserService;
import com.zbutwialypiernik.flixage.service.resource.image.ImageResource;
import lombok.extern.log4j.Log4j2;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Every controller besides that is read-only.
 * User is only able to perform crud calls on playlist object.
 * Everything else like adding new tracks, creating artists, etc, has to be done through admin panel.
 */
@Log4j2
@RestController
@RequestMapping("/playlists")
public class PlaylistController extends QueryableController<Playlist, PlaylistResponse> {

    private final PlaylistService playlistService;
    private final UserService userService;

    private final BoundMapperFacade<Track, TrackResponse> trackMapper;
    private final BoundMapperFacade<PlaylistRequest, Playlist> requestMapper;

    @Autowired
    public PlaylistController(PlaylistService playlistService, UserService userService, MapperFactory mapperFactory) {
        super(playlistService, mapperFactory);
        this.playlistService = playlistService;
        this.userService = userService;
        this.trackMapper = mapperFactory.getMapperFacade(Track.class, TrackResponse.class);
        this.requestMapper = mapperFactory.getMapperFacade(PlaylistRequest.class, Playlist.class);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlaylistResponse create(@Valid @RequestBody PlaylistRequest request, @AuthenticationPrincipal JwtAuthenticationToken authentication) {
        Playlist playlist = requestMapper.map(request);

        User user = userService.findById(authentication.getId()).orElseThrow(() -> {
            log.warn("User: " + authentication.getName() + "#" + authentication.getId() + " is deleted/has fake JWT token");

            return new AuthenticationException("Invalid JWT Token");
        });

        playlist.setOwner(user);
        playlist = playlistService.create(playlist);

        return dtoMapper.map(playlist);
    }

    @PutMapping("/{id}")
    public PlaylistResponse update(@PathVariable String id, @Valid @RequestBody PlaylistRequest request, @AuthenticationPrincipal JwtAuthenticationToken authentication) {
        Playlist oldPlaylist = playlistService.findById(id).orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (isNotOwner(authentication, oldPlaylist)) {
            throw new ResourceForbiddenException();
        }

        Playlist playlist = requestMapper.map(request);
        playlist.setId(id);

        playlist = playlistService.update(playlist);

        return dtoMapper.map(playlist);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id, @AuthenticationPrincipal JwtAuthenticationToken authentication) {
        Playlist oldPlaylist = playlistService.findById(id).orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (isNotOwner(authentication, oldPlaylist)) {
            throw new ResourceForbiddenException();
        }

        playlistService.deleteById(id);
    }

    @GetMapping("/{id}/tracks")
    public List<TrackResponse> getTracks(@PathVariable String id) {
        return playlistService.getTracks(id).stream()
                .map(trackMapper::map)
                .collect(Collectors.toList());
    }

    @PostMapping(value = "/{id}/thumbnail")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadThumbnail(@PathVariable String id, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal JwtAuthenticationToken authentication) {
        Playlist playlist = playlistService.findById(id).orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (file.isEmpty()) {
            throw new BadRequestException("Required file is not included");
        }

        if (file.getSize() > ImageResource.MAX_FILE_SIZE) {
            throw new PayloadTooLargeException("File is too big, limit is " + ImageResource.MAX_FILE_SIZE / FileUtils.ONE_MB + " mb");
        }

        if (isNotOwner(authentication, playlist)) {
            throw new ResourceForbiddenException();
        }

        try {
            playlistService.saveThumbnail(playlist, new ImageResource(file.getBytes(), file.getOriginalFilename(), FilenameUtils.getExtension(file.getOriginalFilename()), file.getContentType()));
        } catch (IOException e) {
            throw new ResourceLoadingException("Problem during upload of file");
        }
    }

    @PutMapping("/{id}/tracks")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addTracks(@PathVariable String id, @RequestBody IdsRequest idsRequest, @AuthenticationPrincipal JwtAuthenticationToken authentication) {
        if (idsRequest.getIds().isEmpty()) {
            throw new BadRequestException("Missing required track ids");
        }

        Playlist playlist = playlistService.findById(id).orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (isNotOwner(authentication, playlist)) {
            throw new ResourceForbiddenException();
        }

        playlistService.addTracks(playlist, idsRequest.getIds());
    }

    @DeleteMapping("/{id}/tracks")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeTracks(@PathVariable String id, @RequestBody IdsRequest idsRequest, @AuthenticationPrincipal JwtAuthenticationToken authentication) {
        if (idsRequest.getIds().isEmpty()) {
            throw new BadRequestException("Missing required track ids");
        }

        Playlist playlist = playlistService.findById(id).orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (isNotOwner(authentication, playlist)) {
            throw new ResourceForbiddenException();
        }

        playlistService.removeTracks(playlist, idsRequest.getIds());
    }

    private boolean isNotOwner(AbstractAuthentication authentication, Playlist playlist) {
        return !playlist.getOwner().getId().equals(authentication.getId());
    }

}
