package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.PageResponse;
import com.zbutwialypiernik.flixage.dto.TrackResponse;
import com.zbutwialypiernik.flixage.dto.UserResponse;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistResponse;
import com.zbutwialypiernik.flixage.entity.Playlist;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.AuthenticationException;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationToken;
import com.zbutwialypiernik.flixage.service.PlaylistService;
import com.zbutwialypiernik.flixage.service.TrackStreamService;
import com.zbutwialypiernik.flixage.service.UserService;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController extends QueryableController<User, UserResponse> {

    private final PlaylistService playlistService;
    private final UserService userService;
    private final TrackStreamService streamService;

    private final BoundMapperFacade<Playlist, PlaylistResponse> playlistMapper;
    private final BoundMapperFacade<Track, TrackResponse> trackMapper;

    public UserController(UserService userService, PlaylistService playlistService, TrackStreamService streamService, MapperFactory mapperFactory) {
        super(userService, mapperFactory);
        this.playlistService = playlistService;
        this.userService = userService;
        this.streamService = streamService;
        this.playlistMapper = mapperFactory.getMapperFacade(Playlist.class, PlaylistResponse.class);
        this.trackMapper = mapperFactory.getMapperFacade(Track.class, TrackResponse.class);
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(@AuthenticationPrincipal JwtAuthenticationToken principal) {
        return dtoMapper.map(userService.findById(principal.getId()).orElseThrow(() -> new AuthenticationException("Invalid JWT Token")));
    }

    @GetMapping("/me/playlists")
    public List<PlaylistResponse> getCurrentUserPlaylist(@AuthenticationPrincipal JwtAuthenticationToken principal) {
        return getUserPlaylists(principal.getId());
    }

    @GetMapping("/{id}/playlists")
    public List<PlaylistResponse> getUserPlaylists(@PathVariable String id) {
        return playlistService.findByUserId(id).stream()
                .map(playlistMapper::map)
                .collect(Collectors.toList());
    }

    @GetMapping("/me/recent")
    public PageResponse<TrackResponse> getLastStreamedTracks(@AuthenticationPrincipal JwtAuthenticationToken principal, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "20") int limit) {
        var page = streamService.findRecentlyStreamedTracks(principal.getId(), offset, limit);

        return new PageResponse<>(page
                .map(stream -> stream.getId().getTrack())
                .map(trackMapper::map)
                .toList(), page.getTotalElements());
    }

}
