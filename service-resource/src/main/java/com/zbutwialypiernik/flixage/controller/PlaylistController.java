package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.TrackResponse;
import com.zbutwialypiernik.flixage.dto.playlist.AddTracksRequest;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistRequest;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistResponse;
import com.zbutwialypiernik.flixage.entity.Playlist;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.ResourceForbiddenException;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationToken;
import com.zbutwialypiernik.flixage.service.PlaylistService;
import com.zbutwialypiernik.flixage.service.QueryableService;
import com.zbutwialypiernik.flixage.service.UserService;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Every controller besides that is read-only.
 * User is only able to perform crud calls on playlist object.
 * Everything else like adding new tracks, creating artists, etc, has to be done through admin panel.
 */
@RestController
@RequestMapping("/playlists")
public class PlaylistController extends QueryableController<Playlist, PlaylistResponse> {

    private final int MB_IN_BYTES = 1048576;

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
        User user = userService.findById(principal.getId());
        playlist.setOwner(user);
        playlistService.create(playlist);

        return dtoMapper.map(playlist);
    }

    @PutMapping("/{id}")
    public PlaylistResponse updatePlaylist(@PathVariable String id, @Valid @RequestBody PlaylistRequest request, @AuthenticationPrincipal JwtAuthenticationToken principal) {
        Playlist playlist = playlistService.findById(id);

        if (isNotOwner(principal, playlist)) {
            throw new ResourceForbiddenException();
        }

        mapperFacade.map(request, playlist);

        playlistService.update(playlist);

        return dtoMapper.map(playlist);
    }

    @DeleteMapping("/{id}")
    public void deletePlaylistById(@PathVariable String id, @AuthenticationPrincipal JwtAuthenticationToken principal) {
        Playlist playlist = playlistService.findById(id);

        if (isNotOwner(principal, playlist)) {
            throw new ResourceForbiddenException();
        }

        playlistService.deleteById(id);
    }

    @GetMapping("/{id}/tracks")
    public List<TrackResponse> getTrackByPlaylistId(@PathVariable String id) {
        Playlist playlist = playlistService.findById(id);

        return playlist.getTracks().stream()
                .map(trackMapper::map)
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/thumbnail")
    public ResponseEntity<String> uploadThumbnail(@PathVariable String id, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal JwtAuthenticationToken principal) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Required file not included");
        }

        if (file.getSize() > 5 * MB_IN_BYTES) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body("File is too big, limit i 5 mb.");
        }

        if (Arrays.stream(QueryableService.ACCEPTED_MIME_TYPES)
                .noneMatch(type -> type.equals(file.getContentType()))) {
            return ResponseEntity.badRequest()
                    .body("Unsupported file format");
        }

        Playlist playlist = playlistService.findById(id);

        if (isNotOwner(principal, playlist)) {
            throw new ResourceForbiddenException();
        }

        playlistService.saveThumbnail(playlist, new File(file.getOriginalFilename()));

        return ResponseEntity.accepted()
                .build();
    }

    @PutMapping("/{id}/tracks")
    public void addTracksToPlaylist(@PathVariable String id, @RequestBody AddTracksRequest addTracksRequest, @AuthenticationPrincipal JwtAuthenticationToken principal) {
        Playlist playlist = playlistService.findById(id);

        if (isNotOwner(principal, playlist)) {
            throw new ResourceForbiddenException();
        }

        playlistService.addTrackToPlaylistByIds(playlist, addTracksRequest.getIds());
    }

    public boolean isNotOwner(JwtAuthenticationToken principal, Playlist playlist) {
        return !playlist.getOwner().getId().equals(principal.getId());
    }

}
