package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.TrackResponse;
import com.zbutwialypiernik.flixage.dto.playlist.AddTracksRequest;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistRequest;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistResponse;
import com.zbutwialypiernik.flixage.entity.Playlist;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.*;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationToken;
import com.zbutwialypiernik.flixage.service.ImageResource;
import com.zbutwialypiernik.flixage.service.PlaylistService;
import com.zbutwialypiernik.flixage.service.QueryableService;
import com.zbutwialypiernik.flixage.service.UserService;
import lombok.extern.log4j.Log4j2;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.*;
import java.util.Arrays;
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

    private final MapperFacade mapperFacade;
    private final BoundMapperFacade<Track, TrackResponse> trackMapper;

    @Autowired
    public PlaylistController(PlaylistService playlistService, UserService userService, MapperFactory mapperFactory) {
        super(playlistService, mapperFactory);
        this.playlistService = playlistService;
        this.userService = userService;
        this.mapperFacade = mapperFactory.getMapperFacade();
        this.trackMapper = mapperFactory.getMapperFacade(Track.class, TrackResponse.class);
    }

    @PostMapping
    public PlaylistResponse create(@Valid @RequestBody PlaylistRequest request, @AuthenticationPrincipal JwtAuthenticationToken principal) {
        Playlist playlist = mapperFacade.map(request, Playlist.class);

        User user = userService.findById(principal.getId()).orElseThrow(() -> {
            // TODO: investigate how to make app more secure.
            log.error("User: " + principal.getName() + "#" + principal.getId() + " is deleted/has fake JWT token");

            return new AuthenticationException("Invalid JWT Token");
        });

        playlist.setOwner(user);
        playlistService.create(playlist);

        return dtoMapper.map(playlist);
    }

    @PutMapping("/{id}")
    public PlaylistResponse updatePlaylist(@PathVariable String id, @Valid @RequestBody PlaylistRequest request, @AuthenticationPrincipal JwtAuthenticationToken principal) {
        Playlist playlist = playlistService.findById(id).orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (isNotOwner(principal, playlist)) {
            throw new ResourceForbiddenException();
        }

        mapperFacade.map(request, playlist);

        playlistService.update(playlist);

        return dtoMapper.map(playlist);
    }

    @DeleteMapping("/{id}")
    public void deletePlaylistById(@PathVariable String id, @AuthenticationPrincipal JwtAuthenticationToken principal) {
        Playlist playlist = playlistService.findById(id).orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (isNotOwner(principal, playlist)) {
            throw new ResourceForbiddenException();
        }

        playlistService.deleteById(id);
    }

    @GetMapping("/{id}/tracks")
    public List<TrackResponse> getTrackByPlaylistId(@PathVariable String id) {
        Playlist playlist = playlistService.findById(id).orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        return playlist.getTracks().stream()
                .map(trackMapper::map)
                .collect(Collectors.toList());
    }

    @PutMapping(value = "/{id}/thumbnail")
    public void uploadThumbnail(@PathVariable String id, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal JwtAuthenticationToken principal) {
        Playlist playlist = playlistService.findById(id).orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (file.isEmpty()) {
            throw new BadRequestException("Required file is not included");
        }

        if (file.getSize() > QueryableService.MAX_FILE_SIZE) {
            throw new PayloadTooLargeException("File is too big, limit is 5 mb.");
        }

        if (isNotOwner(principal, playlist)) {
            throw new ResourceForbiddenException();
        }

        playlistService.saveThumbnail(playlist, new ImageResource() {
            @Override
            public InputStream getInputStream() {
                try {
                    return file.getInputStream();
                } catch (IOException e) {
                    throw new ResourceLoadingException("Problem during upload of file");
                }
            }

            @Override
            public String getName() {
                return file.getOriginalFilename();
            }

            @Override
            public String getExtension() {
                return FilenameUtils.getExtension(file.getOriginalFilename());
            }

            @Override
            public String getMimeType() {
                return file.getContentType();
            }
        });
    }

    @PutMapping("/{id}/tracks")
    public void addTracksToPlaylist(@PathVariable String id, @RequestBody AddTracksRequest addTracksRequest, @AuthenticationPrincipal JwtAuthenticationToken principal) {
        Playlist playlist = playlistService.findById(id).orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (isNotOwner(principal, playlist)) {
            throw new ResourceForbiddenException();
        }

        playlistService.addTrackToPlaylistByIds(playlist, addTracksRequest.getIds());
    }

    public boolean isNotOwner(JwtAuthenticationToken principal, Playlist playlist) {
        return !playlist.getOwner().getId().equals(principal.getId());
    }

}
