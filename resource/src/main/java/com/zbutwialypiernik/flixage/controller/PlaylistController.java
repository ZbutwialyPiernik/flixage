package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.playlist.AddTracksRequest;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistRequest;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistResponse;
import com.zbutwialypiernik.flixage.entity.Playlist;
import com.zbutwialypiernik.flixage.exception.ResourceForbiddenException;
import com.zbutwialypiernik.flixage.filter.JwtUserPrincipal;
import com.zbutwialypiernik.flixage.service.PlaylistService;
import com.zbutwialypiernik.flixage.service.QueryableService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.util.Arrays;

/**
 * Every controller besides that is read-only.
 * User is only able to perform crud calls on playlist object.
 * Everything else like adding new tracks, creating artists, etc, has to be done through admin panel.
 */
@RestController
@RequestMapping("/playlists")
public class PlaylistController extends QueryableController<Playlist, PlaylistResponse> {

    private final PlaylistService service;

    private final MapperFacade mapper;

    @Autowired
    public PlaylistController(PlaylistService service, MapperFacade mapper) {
        super(service);
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public void create(@Valid PlaylistRequest request) {
        Playlist playlist = mapper.map(request, Playlist.class);

        service.create(playlist);
    }

    @PutMapping("/{id}")
    public void updatePlaylist(String id, @Valid PlaylistRequest request) {
        Playlist playlist = mapper.map(request, Playlist.class);

        service.update(playlist);
    }

    @DeleteMapping("/{id}")
    public void deletePlaylistById(String id, @AuthenticationPrincipal JwtUserPrincipal principal) {
        Playlist playlist = service.findById(id);

        if (isNotOwner(principal, playlist)) {
            throw new ResourceForbiddenException();
        }

        service.deleteById(id);
    }

    @PostMapping("/{id}/thumbnail")
    public ResponseEntity<String> uploadThumbnail(String id, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal JwtUserPrincipal principal) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("File not included");
        }

        if (Arrays.stream(QueryableService.ACCEPTED_MIME_TYPES)
                .noneMatch(type -> type.equals(file.getContentType()))) {
            return ResponseEntity.badRequest()
                    .body("Unsupported file format");
        }

        Playlist playlist = service.findById(id);

        if (isNotOwner(principal, playlist)) {
            throw new ResourceForbiddenException();
        }

        service.saveThumbnail(playlist, new File(file.getOriginalFilename()));

        return ResponseEntity.accepted()
                .build();
    }

    @PutMapping("/{id}/tracks")
    public void addTracksToPlaylist(@PathVariable String id, @RequestBody AddTracksRequest addTracksRequest, @AuthenticationPrincipal JwtUserPrincipal principal) {
        Playlist playlist = service.findById(id);

        if (isNotOwner(principal, playlist)) {
            throw new ResourceForbiddenException();
        }

        service.addTrackToPlaylistByIds(playlist, addTracksRequest.getIds());
    }

    public boolean isNotOwner(JwtUserPrincipal principal, Playlist playlist) {
        // Service should throw exception if playlist is null, we check for null only to be sure.
        return playlist == null || !playlist.getOwner().getId().equals(principal.getId());
    }

    @Override
    public PlaylistResponse toResponse(Playlist playlist) {
        return mapper.map(playlist, PlaylistResponse.class);
    }

}
