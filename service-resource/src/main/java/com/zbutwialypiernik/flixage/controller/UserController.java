package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.UserResponse;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistResponse;
import com.zbutwialypiernik.flixage.entity.Playlist;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.AuthenticationException;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationToken;
import com.zbutwialypiernik.flixage.repository.ImageFileStore;
import com.zbutwialypiernik.flixage.service.PlaylistService;
import com.zbutwialypiernik.flixage.service.UserService;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController extends QueryableController<User, UserResponse> {
    
    private final PlaylistService playlistService;
    private final UserService userService;

    private final BoundMapperFacade<Playlist, PlaylistResponse> playlistMapper;

    public UserController(UserService userService, PlaylistService playlistService, ImageFileStore imageFileStore, MapperFactory mapperFactory) {
        super(userService, mapperFactory);
        this.playlistService = playlistService;
        this.userService = userService;
        this.playlistMapper = mapperFactory.getMapperFacade(Playlist.class, PlaylistResponse.class);
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

}
