package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.ArtistResponse;
import com.zbutwialypiernik.flixage.dto.PageResponse;
import com.zbutwialypiernik.flixage.dto.TrackResponse;
import com.zbutwialypiernik.flixage.dto.UserResponse;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistResponse;
import com.zbutwialypiernik.flixage.entity.Artist;
import com.zbutwialypiernik.flixage.entity.Playlist;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.AuthenticationException;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import com.zbutwialypiernik.flixage.security.AbstractAuthentication;
import com.zbutwialypiernik.flixage.service.PlaylistService;
import com.zbutwialypiernik.flixage.service.ShareCodeGenerator;
import com.zbutwialypiernik.flixage.service.TrackStreamService;
import com.zbutwialypiernik.flixage.service.UserService;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController extends QueryableController<User, UserResponse> {

    private static final String SHARE_CODE_REGEX = "[" + ShareCodeGenerator.ALPHABET + "]{6}";

    private final PlaylistService playlistService;
    private final UserService userService;
    private final TrackStreamService streamService;

    private final BoundMapperFacade<Playlist, PlaylistResponse> playlistMapper;
    private final BoundMapperFacade<Track, TrackResponse> trackMapper;
    private final BoundMapperFacade<Artist, ArtistResponse> artistMapper;

    public UserController(UserService userService, PlaylistService playlistService, TrackStreamService streamService, MapperFactory mapperFactory) {
        super(userService, mapperFactory);
        this.playlistService = playlistService;
        this.userService = userService;
        this.streamService = streamService;
        this.playlistMapper = mapperFactory.getMapperFacade(Playlist.class, PlaylistResponse.class);
        this.trackMapper = mapperFactory.getMapperFacade(Track.class, TrackResponse.class);
        this.artistMapper = mapperFactory.getMapperFacade(Artist.class, ArtistResponse.class);
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(@AuthenticationPrincipal AbstractAuthentication principal) {
        return dtoMapper.map(userService.findById(principal.getId()).orElseThrow(() -> new AuthenticationException("Invalid JWT Token")));
    }

    @GetMapping("/me/playlists")
    public List<PlaylistResponse> getCurrentUserPlaylist(@AuthenticationPrincipal AbstractAuthentication principal) {
        return getUserPlaylists(principal.getId());
    }

    @GetMapping("/{id}/playlists")
    public List<PlaylistResponse> getUserPlaylists(@PathVariable String id) {
        return playlistService.findByUserId(id).stream()
                .map(playlistMapper::map)
                .collect(Collectors.toList());
    }

    @GetMapping("/me/recentlyListened")
    public PageResponse<TrackResponse> getLastStreamedTracks(@AuthenticationPrincipal AbstractAuthentication principal, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "20") int limit) {
        final var page = streamService.findRecentlyStreamedTracks(principal.getId(), offset, limit);

        return new PageResponse<>(page
                .map(stream -> stream.getId().getTrack())
                .map(trackMapper::map)
                .toList(), page.getTotalElements());
    }

    /**
     * Returns most listened artist within last 30 days
     */
    @GetMapping("/me/mostListened")
    public PageResponse<ArtistResponse> getMostListenedArtists(@AuthenticationPrincipal AbstractAuthentication principal, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "20") int limit) {
        final var page= streamService.findMostStreamedArtists(principal.getId(), offset, limit);

        return new PageResponse<>(page.stream()
                .map(artistMapper::map)
                .collect(Collectors.toList()), page.getTotalElements());
    }

    @GetMapping("/me/followedPlaylists")
    public List<PlaylistResponse> getFollowedPlaylists(@AuthenticationPrincipal AbstractAuthentication principal) {
        final var user = userService.findById(principal.getId()).orElseThrow(ResourceNotFoundException::new);

        return user.getObservedPlaylists().stream()
                .map(playlistMapper::map)
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/me/followedPlaylists/{shareCode}")
    public void followPlaylist(@AuthenticationPrincipal AbstractAuthentication principal, @PathVariable @Valid @Pattern(regexp = SHARE_CODE_REGEX) String shareCode) {
        userService.followPlaylist(principal.getId(), shareCode);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/me/followedPlaylists/{shareCode}")
    public void unfollowPlaylist(@AuthenticationPrincipal AbstractAuthentication principal, @PathVariable @Valid @Pattern(regexp = SHARE_CODE_REGEX) String shareCode) {
        userService.unfollowPlaylist(principal.getId(), shareCode);
    }

}
