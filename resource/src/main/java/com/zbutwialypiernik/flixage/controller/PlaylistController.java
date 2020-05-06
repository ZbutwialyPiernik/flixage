package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.config.GatewayLinkGenerator;
import com.zbutwialypiernik.flixage.dto.playlist.AddTracksRequest;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistRequest;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistResponse;
import com.zbutwialypiernik.flixage.entity.Playlist;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.ResourceForbiddenException;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationToken;
import com.zbutwialypiernik.flixage.service.PlaylistService;
import com.zbutwialypiernik.flixage.service.QueryableService;
import com.zbutwialypiernik.flixage.service.UserService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    private final int MB_IN_BYTES = 1048576;

    private final PlaylistService playlistService;
    private final UserService userService;

    private final MapperFacade mapper;
    private final GatewayLinkGenerator linkGenerator;

    @Autowired
    public PlaylistController(PlaylistService playlistService, UserService userService, MapperFacade mapper, GatewayLinkGenerator linkGenerator) {
        super(playlistService);
        this.playlistService = playlistService;
        this.userService = userService;
        this.mapper = mapper;
        this.linkGenerator = linkGenerator;
    }

    @PostMapping
    public PlaylistResponse create(@Valid @RequestBody PlaylistRequest request, @AuthenticationPrincipal JwtAuthenticationToken principal) {
        Playlist playlist = mapper.map(request, Playlist.class);
        User user = userService.findById(principal.getId());
        playlist.setOwner(user);
        playlistService.create(playlist);

        return toResponse(playlist);
    }

    @PutMapping("/{id}")
    public PlaylistResponse updatePlaylist(@PathVariable String id, @Valid @RequestBody PlaylistRequest request, @AuthenticationPrincipal JwtAuthenticationToken principal) {
        Playlist playlist = playlistService.findById(id);

        if (isNotOwner(principal, playlist)) {
            throw new ResourceForbiddenException();
        }

        mapper.map(request, playlist);

        playlistService.update(playlist);

        return toResponse(playlist);
    }

    @DeleteMapping("/{id}")
    public void deletePlaylistById(@PathVariable String id, @AuthenticationPrincipal JwtAuthenticationToken principal) {
        Playlist playlist = playlistService.findById(id);

        if (isNotOwner(principal, playlist)) {
            throw new ResourceForbiddenException();
        }

        playlistService.deleteById(id);
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
        // Service should throw exception if playlist is null, we check for null only to be sure.
        return playlist == null || !playlist.getOwner().getId().equals(principal.getId());
    }

    @Override
    public PlaylistResponse toResponse(Playlist playlist) {
        PlaylistResponse response =  mapper.map(playlist, PlaylistResponse.class);
        response.setThumbnailUrl(linkGenerator.generateLink("playlists/" + playlist.getId() + "/thumbnail"));

        return response;
    }

}
